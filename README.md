# HarmonyOS QR Code Generation
![QRCodegeneration.](/assets/QRCodegeneration.png "QRCodegeneration")

## Introduction
A **QR code** (abbreviated from **Quick Response code**) is a type of matrix barcode (or two-dimensional barcode).
HarmonyOS provides code generation to return the byte stream of a quick response (QR) code image based on a given string and QR code image size. The caller can use the QR code byte stream to generate a QR code image.

### When to Use
You can use this capability to generate QR code images based on a given string. Common application scenarios include:
* Social or communication applications: generating a QR code for contacts based on the input information 
* Shopping or payment applications: generating a QR code for online payment based on the input link.

## How to Develop
### Create UI
It is a very simple user interfaz, is just a button and a View where we will print the QR Code Image.
```
<?xml version="1.0" encoding="utf-8"?>
<DirectionalLayout
    xmlns:ohos="http://schemas.huawei.com/res/ohos"
    ohos:height="match_parent"
    ohos:width="match_parent"
    ohos:orientation="vertical"
    ohos:padding="$float:margin">
 
    <Button
        ohos:id="$+id:getCodeButton"
        ohos:height="match_content"
        ohos:width="match_parent"
        ohos:background_element="$graphic:background_button"
        ohos:padding="$float:marginS"
        ohos:text="$string:getGenCodeButtonLabel"
        ohos:text_size="$float:buttonTextSize"
        ohos:top_margin="$float:margin"/>
 
    <Image
        ohos:id="$+id:qrCodeImage"
        ohos:height="match_content"
        ohos:width="match_parent"
        ohos:top_margin="$float:margin"/>
 
</DirectionalLayout>
```

![Main Screen.](/assets/screen1.jpg "Main Screen")

In the **MainAbilitySlice** implements **ConnectionCallback** interface to implement the operation after a connection with the capability engine is successfully established or fails to be established.

import ohos.aafwk.ability.AbilitySlice;
import ohos.ai.cv.common.ConnectionCallback;
 
public class MainAbilitySlice extends AbilitySlice implements ConnectionCallback {
```
private boolean isConnect = false;
 
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
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
```

Call the **VisionManager.init()** method based on **context** and **connectionCallback** to establish a connection with the capability engine. Ensure that **context** is an **ohos.aafwk.ability.Ability** or **ohos.aafwk.ability.AbilitySlice** instance, or their child class instance.
```
VisionManager.init(MainAbilitySlice.this, this);
```

Obtain an **IBarcodeDetector** object with the context of your project passed to the context parameter.
```
IBarcodeDetector barcodeDetector = VisionManager.getBarcodeDetector(MainAbilitySlice.this);
```

Define the size of the expected QR code image and allocate the space for the byte stream array based on the image size.
```
int QR_LENGTH = 400;
byte[] bytes = new byte[QR_LENGTH * QR_LENGTH * 4];
```

Call the detect() method on **barcodeDetector** to generate the byte stream of the QR code image based on the given string.
```
int detect = barcodeDetector.detect("https://developer.harmonyos.com/en", bytes, QR_LENGTH, QR_LENGTH);
```

If the return value is **HwHiAIResultCode.AIRESULT_SUCCESS (0)**, the method is successfully called and you can print the QR Code in the Image Component  as PixelMap.
```
if (detect == HwHiAIResultCode.AIRESULT_SUCCESS) {
    ImageSource imageSource = ImageSource.create(bytes, null);
    PixelMap pixelmap = imageSource.createPixelmap(null);
    qrImage.setPixelMap(pixelmap);
    barcodeDetector.release();
}
```

Call the **VisionManager.destroy()** method to disconnect from the code generation capability engine
```
VisionManager.destroy();
```

The complete MainAbilitySlice is as follow: 
```
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
```

We have the next result: 

![Main Screen 2.](/assets/screen2.jpg "Main Screen 2")

## Tips and Tricks
Only QR codes can be generated. Due to the restrictions of the QR code algorithm, the input string cannot exceed 2953 characters.
* The width of the expected QR code image cannot exceed 1920 pixels, and the height cannot exceed 1680 pixels. As a QR code is a type of square matrix barcode, it is recommended that QR code images be square. If QR code images are rectangular, blank areas will be left around QR code information.
* To install in a real HarmonyOS device you need add the UDID on the AGC, a UDID is a string of 64 characters, including letters and digits

Go to Settings > About on your device and keep tapping Build number until a message indicating that you have entered the developer mode is displayed.
Connect the PC to the device and then start the command line tool on the PC. After **hdc shell** is displayed, run **bm get --udid** to obtain the UDID of the watch. (Also **adb shell** command is available)

## References:
QR Code Wiki: (https://en.wikipedia.org/wiki/QR_code)
Code Generation: (https://developer.harmonyos.com/en/docs/documentation/doc-guides/ai-code-genration-overview-0000001051062161)