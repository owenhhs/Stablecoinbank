package net.lab1024.sa.admin.module.business.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * 兑付订单 人工处理
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
public class CashOrderManualForm {
    @Schema(description = "出金订单表ID")
    private long id;

    @Schema(description = "出金订单编号")
    @NotNull(message = "出金订单编号不能为空")
    private String orderNo;

    @Schema(description = "人工处理备注")
    private String manualRemark;


    @Schema(description = "拆单集合")
    @Size(min = 1, max = 10, message = "最小为1笔，最多拆10笔")
    @Valid
    private List<SubOrder> subOrderList;

    /**
     * 子订单DTO类
     */
    @Data
    public static class SubOrder {

        /**
         * 渠道类型 1-自有对接渠道、2-外部渠道
         */
        @Schema(description = "渠道类型")
        @NotNull(message = "渠道类型不能为空")
        private Integer chnlType;

        /**
         * 渠道Id
         */
        @Schema(description = "渠道编号")
//        @NotNull(message = "渠道编号")
        private Long channelId;

        /**
         * 出金金额
         */
        @Schema(description = "出金金额")
        @NotNull(message = "出金金额不能为空")
        private BigDecimal amount;

        /**
         * 第三方订单编号，当渠道类型 = 2时，可填写
         */
        @Schema(description = "渠道订单编号")
        private String thirdpartyOrderNo;

        /**
         * 支付单据文件id，当渠道类型 = 2时，上传
         */
        @Schema(description = "支付单据文件id")
        private Long billFileId;

        /**
         * 完成时间，当渠道类型 = 2时，填写
         */
        @Schema(description = "出金完成时间")
        private Long finishedTime;

    }

}