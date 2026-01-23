package com.shaxian.biz.dto.product.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品分享码响应DTO
 */
@Schema(description = "商品分享码响应")
public class ShareCodeResponse {

    @Schema(description = "分享码", example = "eyJwcm9kdWN0SWQiOjEsInRlbmFudElkIjoxLCJub25jZSI6Inh4eHh4eHh4eHh4eCIsInRpbWVzdGFtcCI6MTcwNjAwMDAwMCwic2lnbmF0dXJlIjoieHh4eHh4eHh4eHh4eHh4eCJ9")
    private String shareCode;

    public ShareCodeResponse() {
    }

    public ShareCodeResponse(String shareCode) {
        this.shareCode = shareCode;
    }

    public String getShareCode() {
        return shareCode;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }
}
