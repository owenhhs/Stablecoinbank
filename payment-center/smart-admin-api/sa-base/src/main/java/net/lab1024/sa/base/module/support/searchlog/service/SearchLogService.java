package net.lab1024.sa.base.module.support.searchlog.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.enumeration.LogTypeEnum;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartLocalDateUtil;
import net.lab1024.sa.base.module.support.searchlog.domain.SearchLogRes;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author 孙宇
 * @date 2025/03/17 12:00
 */
@Slf4j
@Service
public class SearchLogService {

    @Value("${project.log-directory}")
    private String logDirectory;

    private static final Pattern SAFE_PATTERN = Pattern.compile("^[a-zA-Z0-9\\u4e00-\\u9fa5\\u3000-\\u303F\\uff00-\\uffef ,.;\"'_-|\\\\]+$");
    private static final String DATE_REGEX = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$";
    private static final Pattern DATE_PATTERN = Pattern.compile(DATE_REGEX);

    public ResponseDTO<List<SearchLogRes>> search(String keyword, String date, Integer count, String type) {
        if (StringUtils.isEmpty(keyword)) {
            return ResponseDTO.ok();
        }
        if (!EnumUtils.isValidEnum(LogTypeEnum.class, type)) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }
        if (StringUtils.isNotEmpty(date) && !DATE_PATTERN.matcher(date).matches()) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }
        String sanitizedKeyword = sanitizeInput(keyword);
        try {
            Process process = getProcess(keyword, date, count, type);

            List<SearchLogRes> result = getStrings(process);
            // 6. 等待命令完成（可设置超时）
            boolean isFinished = process.waitFor(10, TimeUnit.SECONDS);
            if (!isFinished) {
                process.destroy(); // 强制终止命令
                log.error("Command timed out. keyword: {}, sanitizedKeyword:{}", keyword, sanitizedKeyword);
                return ResponseDTO.ok(result);
            }
            // 7. 获取退出码
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                log.error("Command failed. Exit Code: {}, keyword: {}, sanitizedKeyword:{}", exitCode, keyword, sanitizedKeyword);
            }
            return ResponseDTO.ok(result);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("执行命令异常：{}, keyword: {}, sanitizedKeyword:{}", e.getMessage(), keyword, sanitizedKeyword, e);
            throw new BusinessException(UserErrorCode.COMMAND_RUNNING_ERROR);
        }
    }

    @NotNull
    private Process getProcess(String keyword, String date, Integer count, String type) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        String path;
        if (LogTypeEnum.all.equalsValue(type)) {
            path = "*";
        } else {
            path = type;
        }
        String catCommand;
        if (StringUtils.isEmpty(date)) {
            catCommand = String.format("find %s -type d \\( -path \"tomcat-logs\" -o -path \"slow-sql\" \\) -prune -o -name \"*.log\" -exec cat {} +", path);
        } else if (SmartLocalDateUtil.isCurrentDate(date)) {
            catCommand = String.format("find %s -type d \\( -path \"tomcat-logs\" -o -path \"slow-sql\" \\) -prune -o -name \"*.log\" ! -name \"*-????-??-??-?.log\" -exec cat {} +", path);
        } else {
            catCommand = String.format("find %s -type d \\( -path \"tomcat-logs\" -o -path \"slow-sql\" \\) -prune -o -name \"*-%s-?.log\" -exec cat {} +", path, date);
        }

        String[] command = {
                "/bin/sh", "-c",
                String.format("%s | grep -- '%s' | tail -n %s", catCommand, keyword, count)
        };
        log.debug("执行命令：{}", String.join(" | ", command));

        // Linux/macOS
        processBuilder.command(command);
        // Windows
        // processBuilder.command("cmd.exe", "/c", "dir");

        // 2. 设置工作目录（可选）
        processBuilder.directory(new File(logDirectory));

        // 3. 启动进程
        return processBuilder.start();
    }

    @NotNull
    private static List<SearchLogRes> getStrings(Process process) throws IOException {
        List<SearchLogRes> result = new ArrayList<>();
        // 4. 读取输出流
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            result.add(logStringToSearchLogRes(line));
        }
        // 5. 读取错误流
        StringBuilder errorString = new StringBuilder();
        InputStream errorStream = process.getErrorStream();
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
        while ((line = errorReader.readLine()) != null) {
            errorString.append(line);
        }
        if (errorString.length() > 0) {
            if (errorString.toString().contains("No such file or directory")) {
                throw new BusinessException(UserErrorCode.LOG_FILE_NOT_EXIST);
            }
            log.error("Error occurred during command execution: {}", errorString);
            throw new BusinessException(UserErrorCode.COMMAND_RUNNING_ERROR);
        }
        // result根据ts倒序排序
        result.sort((o1, o2) -> {
            if (o1.getTs() == null && o2.getTs() == null) {
                return 0;
            } else if (o2.getTs() == null) {
                return -1;
            } else if (o1.getTs() == null) {
                return 1;
            }
            return o2.getTs().compareTo(o1.getTs());
        });
        return result;
    }

    private static SearchLogRes logStringToSearchLogRes(String logString) {
        try {
            return JSONObject.parseObject(logString, SearchLogRes.class);
        } catch (Exception e) {
            SearchLogRes searchLogRes = new SearchLogRes();
            searchLogRes.setMsg(logString);
            return searchLogRes;
        }
    }


    private static String sanitizeInput(String userInput) throws IllegalArgumentException {
        if (!SAFE_PATTERN.matcher(userInput).matches()) {
            throw new BusinessException(UserErrorCode.INPUT_CONTENT_ILLEGAL);
        }
        return "'" + userInput.replace("'", "'\"'\"'") + "'";
    }

}
