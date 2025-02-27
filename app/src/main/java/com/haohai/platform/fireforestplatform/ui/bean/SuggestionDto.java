
package com.haohai.platform.fireforestplatform.ui.bean;

public class SuggestionDto {
    int page;
    int limit;
    SuggestionDto.Dto dto;

    public SuggestionDto(int page, int limit, Dto dto) {
        this.page = page;
        this.limit = limit;
        this.dto = dto;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Dto getDto() {
        return dto;
    }

    public void setDto(Dto dto) {
        this.dto = dto;
    }

    public static class Dto{
        String userId;

        public Dto(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

}
