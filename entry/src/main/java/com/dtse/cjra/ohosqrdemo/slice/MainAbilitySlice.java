package com.dtse.cjra.ohosqrdemo.slice;

import com.dtse.cjra.ohosqrdemo.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Image;
import ohos.ai.cv.common.ConnectionCallback;
import ohos.ai.cv.common.VisionManager;
import ohos.ai.cv.qrcode.IBarcodeDetector;
import ohos.ai.engine.resultcode.HwHiAIResultCode;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;

public class MainAbilitySlice extends AbilitySlice implements ConnectionCallback {

    private Image qrImage;
    private boolean isConnect = false;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        initViews();

        VisionManager.init(MainAbilitySlice.this, this);
    }

    private void initViews() {
        qrImage = (Image) findComponentById(ResourceTable.Id_qrCodeImage);
        findComponentById(ResourceTable.Id_getCodeButton).setClickedListener(click -> createQrCode());
    }

    private void createQrCode() {
        if (isConnect) {
            int QR_LENGTH = 400;
            byte[] bytes = new byte[QR_LENGTH * QR_LENGTH * 4];
            IBarcodeDetector barcodeDetector = VisionManager.getBarcodeDetector(MainAbilitySlice.this);
            int detect = barcodeDetector.detect("https://developer.harmonyos.com/en", bytes, QR_LENGTH, QR_LENGTH);
            if (detect == HwHiAIResultCode.AIRESULT_SUCCESS) {
                ImageSource imageSource = ImageSource.create(bytes, null);
                PixelMap pixelmap = imageSource.createPixelmap(null);
                qrImage.setPixelMap(pixelmap);
                barcodeDetector.release();
            }
        }
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        VisionManager.destroy();
    }

    @Override
    public void onServiceConnect() {
        isConnect = true;
    }

    @Override
    public void onServiceDisconnect() {
        isConnect = false;
    }
}
