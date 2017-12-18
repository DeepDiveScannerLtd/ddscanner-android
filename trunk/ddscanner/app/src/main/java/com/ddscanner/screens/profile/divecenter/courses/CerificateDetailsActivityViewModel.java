package com.ddscanner.screens.profile.divecenter.courses;


import com.ddscanner.entities.Certificate;

public class CerificateDetailsActivityViewModel {

    private Certificate certificate;

    public CerificateDetailsActivityViewModel(Certificate certificate) {
        this.certificate = certificate;
    }

    public Certificate getCertificate() {
        return certificate;
    }
}
