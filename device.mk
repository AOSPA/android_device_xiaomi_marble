#
# Copyright (C) 2021 The Android Open Source Project
#           (C) 2022-2024 Paranoid Android
#
# SPDX-License-Identifier: Apache-2.0
#

# Inherit from sm8450-common
$(call inherit-product, device/xiaomi/sm8450-common/common.mk)

# Audio
$(call soong_config_set_bool,android_hardware_audio,skip_speaker_layout_channel_mask_field,true)

# Camera
PRODUCT_SYSTEM_PROPERTIES += \
    ro.product.mod_device=marble_global

# Characteristics
PRODUCT_CHARACTERISTICS := nosdcard

# Display / Graphics
PRODUCT_COPY_FILES += \
$(foreach did, 4630946370515662721 4630946370515662722 4630946480857061761 4630946480857061762, \
    $(LOCAL_PATH)/configs/displayconfig.xml:$(TARGET_COPY_OUT_VENDOR)/etc/displayconfig/display_id_$(did).xml)

PRODUCT_VENDOR_PROPERTIES += \
    debug.sf.defer_refresh_rate_when_off=1 \
    vendor.display.enable_fp_monitor=1 \
    vendor.display.enable_hist_intr=1 \
    vendor.display.idle_time=0

# Dolby
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/configs/dolby/dax-default.xml:$(TARGET_COPY_OUT_VENDOR)/etc/dolby/dax-default.xml

# Fingerprint
TARGET_USES_MFP_DAEMON := true

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
    persist.nfc.camera.pause_polling=true \
    persist.nfc_cfg.config_file_name=libnfc-nci.conf

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
