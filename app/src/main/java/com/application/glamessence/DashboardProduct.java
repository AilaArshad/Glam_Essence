package com.application.glamessence;
public class DashboardProduct {
    private String productId;
    private String category;
    private String productName;
    private String subHeading;
    private String url;

    public DashboardProduct(String productId, String category, String productName, String subHeading, String url) {
        this.productId = productId;
        this.category = category;
        this.productName = productName;
        this.subHeading = subHeading;
        this.url = url;
    }

    public String getProductId() { return productId; }
    public String getCategory() { return category; }
    public String getProductName() { return productName; }
    public String getSubHeading() { return subHeading; }
    public String getUrl() { return url; }
}
