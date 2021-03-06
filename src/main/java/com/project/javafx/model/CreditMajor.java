package com.project.javafx.model;

import java.util.ArrayList;
import java.util.List;

//change the name to department
public class CreditMajor {

    private final String majorCode;
    private final String majorTitle;
    private final int majorCreditsRequired;
    private final int minorCreditsRequired;

    private List<CreditCourse> majorCatalog = new ArrayList<>();
    private List<CreditCourse> minorCatalog = new ArrayList<>();

    public CreditMajor(String majorCode, String majorTitle, int majorCreditsRequired, int minorCreditsRequired) {
        this.majorCode = majorCode;
        this.majorTitle = majorTitle;
        this.majorCreditsRequired = majorCreditsRequired;
        this.minorCreditsRequired = minorCreditsRequired;
    }

    // GETTER AND SETTER
    public String getMajorCode() {
        return majorCode;
    }

    public String getMajorTitle() {
        return majorTitle;
    }

    public int getMajorCreditsRequired() {
        return majorCreditsRequired;
    }

    public int getMinorCreditsRequired() {
        return minorCreditsRequired;
    }

    public List<CreditCourse> getMajorCatalog() {
        return majorCatalog;
    }

    public List<CreditCourse> getMinorCatalog() {
        return minorCatalog;
    }

    public void createMajorCatalog(List<CreditCourse> majorCatalog) {
        this.majorCatalog = majorCatalog;
    }

    public void createMinorCatalog(List<CreditCourse> minorCatalog) {
        this.minorCatalog = minorCatalog;
    }

    @Override
    public String toString() {
        return majorTitle;
    }

}
