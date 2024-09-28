#
# Copyright (C) 2021 The Android Open Source Project
#           (C) 2022-2024 Paranoid Android
#
# SPDX-License-Identifier: Apache-2.0
#

# Inherit from sm8450-common
$(call inherit-product, device/xiaomi/sm8450-common/common.mk)

# Camera
PRODUCT_SYSTEM_PROPERTIES += \
    ro.product.mod_device=marble_global

# Characteristics
PRODUCT_CHARACTERISTICS := nosdcard

# Display / Graphics
PRODUCT_VENDOR_PROPERTIES += \
    debug.sf.defer_refresh_rate_when_off=1 \
    vendor.display.enable_fp_monitor=1 \
    vendor.display.enable_hist_intr=1 \
    vendor.display.idle_time=0

# Fingerprint
TARGET_USES_MFP_DAEMON := true

PRODUCT_PACKAGES += \
    android.hardware.biometrics.fingerprint@2.1.vendor \
    vendor.xiaomi.hardware.fx.tunnel@1.0.vendor

# Firmware
$(call inherit-product-if-exists, vendor/xiaomi/firmware/marble/config.mk)

# Init scripts
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/rootdir/bin/init.marble.sh:$(TARGET_COPY_OUT_VENDOR)/bin/init.marble.sh \
    $(LOCAL_PATH)/rootdir/etc/init.marble.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/init.marble.rc

# Kernel
KERNEL_PREBUILT_DIR := $(LOCAL_PATH)-kernel

# Keylayout
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/configs/keylayout/uinput-fpc.kl:$(TARGET_COPY_OUT_VENDOR)/usr/keylayout/uinput-fpc.kl \
    $(LOCAL_PATH)/configs/keylayout/uinput-goodix.kl:$(TARGET_COPY_OUT_VENDOR)/usr/keylayout/uinput-goodix.kl

# NFC
TARGET_NFC_SKU := marble

PRODUCT_SYSTEM_EXT_PROPERTIES += \
    persist.nfc.camera.pause_polling=true

# Namespaces
PRODUCT_SOONG_NAMESPACES += \
    $(LOCAL_PATH)

# Overlays
PRODUCT_PACKAGES += \
    AOSPAMarbleFrameworksOverlay \
    MarbleApertureOverlay \
    MarbleCNSettingsOverlay \
    MarbleCNSettingsProviderOverlay \
    MarbleCNWifiOverlay \
    MarbleCNWifiMainlineOverlay \
    MarbleFrameworksOverlay \
    MarbleGLSettingsOverlay \
    MarbleGLSettingsProviderOverlay \
    MarbleGLWifiOverlay \
    MarbleGLWifiMainlineOverlay \
    MarbleINSettingsOverlay \
    MarbleINSettingsProviderOverlay \
    MarbleINWifiOverlay \
    MarbleINWifiMainlineOverlay \
    MarbleNfcOverlay \
    MarbleSettingsOverlay \
    MarbleSystemUIOverlay

# Shipping API
PRODUCT_SHIPPING_API_LEVEL := 33

# Vendor blobs
$(call inherit-product, vendor/xiaomi/marble/marble-vendor.mk)
