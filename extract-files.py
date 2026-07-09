#!/usr/bin/env -S PYTHONPATH=../../../tools/extract-utils python3
#
# SPDX-FileCopyrightText: 2024 The LineageOS Project
# SPDX-License-Identifier: Apache-2.0
#

from extract_utils.fixups_blob import blob_fixup, blob_fixups_user_type
from extract_utils.fixups_lib import (
    lib_fixup_remove,
    lib_fixups as base_lib_fixups,
    lib_fixups_user_type,
)
from extract_utils.main import ExtractUtils, ExtractUtilsModule

namespace_imports = [
    'device/xiaomi/sm8450-common',
    'hardware/qcom/display',
    'hardware/qcom/display/gralloc',
    'hardware/qcom/display/libdebug',
    'hardware/xiaomi',
    'vendor/qcom/common/vendor/adreno/s',
    'vendor/qcom/common/vendor/display/5.10',
    'vendor/qcom/common/vendor/media/5.10',
    'vendor/qcom/common/vendor/perf',
    'vendor/qcom/common/vendor/wlan',
    'vendor/xiaomi/sm8450-common',
]


def lib_fixup_vendor_suffix(lib: str, partition: str, *args, **kwargs):
    return f'{lib}_{partition}' if partition == 'vendor' else None


def lib_fixup_prebuilt_suffix(lib: str, *args, **kwargs):
    return f'{lib}_prebuilt'


def lib_fixup_xiaomi_suffix(lib: str, *args, **kwargs):
    return f'{lib}_xiaomi'


lib_fixups: lib_fixups_user_type = {
    **base_lib_fixups,
    'audio.primary.taro': lib_fixup_xiaomi_suffix,
    'libgrpc++_unsecure': lib_fixup_prebuilt_suffix,
    (
        'com.qualcomm.qti.dpm.api@1.0',
        'com.qualcomm.qti.imscmservice*',
        'com.qualcomm.qti.uceservice*',
        'vendor.qti.data.*',
        'vendor.qti.diaghal@1.0',
        'vendor.qti.hardware.data.*',
        'vendor.qti.hardware.dpmservice*',
        'vendor.qti.hardware.embmssl*',
        'vendor.qti.hardware.limits*',
        'vendor.qti.hardware.ListenSoundModel@1.0',
        'vendor.qti.hardware.mwqemadapter@1.0',
        'vendor.qti.hardware.qccsyshal*',
        'vendor.qti.hardware.qccvndhal@1.0',
        'vendor.qti.hardware.radio.*',
        'vendor.qti.hardware.slmadapter@1.0',
        'vendor.qti.hardware.wifidisplaysession@1.0',
        'vendor.qti.imsrtpservice@3.0',
        'vendor.qti.ims.*',
        'vendor.qti.latency*',
        'vendor.xiaomi.hardware.campostproc@1.0',
        'vendor.xiaomi.hardware.displayfeature@1.0',
    ): lib_fixup_vendor_suffix,
    'libwpa_client': lib_fixup_remove,
}

blob_fixups: blob_fixups_user_type = {
    (
        'vendor/lib64/libagm.so',
        'vendor/lib64/libar-pal.so',
        'vendor/lib64/libfmpal.so',
        'vendor/lib64/libkaraokepal.so',
        'vendor/lib64/libmcs.so',
        'vendor/lib64/libaudioroute_ext.so',
        'vendor/lib64/libmmhardware.so',
    ): blob_fixup().replace_needed('libaudioroute.so', 'libaudioroute-v34.so'),
    (
        'vendor/bin/hw/mfp-daemon',
        'vendor/lib64/hw/audio.primary.taro.so',
        'vendor/lib64/hw/displayfeature.default.so',
        'vendor/lib64/libmi-stc-HW-modulate.so',
        'vendor/lib64/libmiBrightness.so',
        'vendor/lib64/soundfx/libmisoundfx.so',
    ): blob_fixup().replace_needed('libstagefright_foundation.so', 'libstagefright_foundation-v33.so'),
    'vendor/bin/hw/vendor.qti.hardware.display.composer-service': blob_fixup()
        .remove_needed('libutils.so')
        .add_needed('libutils-v32.so')
        .add_needed('libutils-shim.so')
        .replace_needed('android.hardware.common-V2-ndk_platform.so', 'android.hardware.common-V2-ndk.so')
        .replace_needed('vendor.qti.hardware.display.config-V5-ndk_platform.so', 'vendor.qti.hardware.display.config-V5-ndk.so'),
    (
        'vendor/etc/camera/marble_enhance_motiontuning.xml',
        'vendor/etc/camera/marble_motiontuning.xml',
    ): blob_fixup().regex_replace('xml=version', 'xml version'),
    'vendor/etc/camera/pureView_parameter.xml': blob_fixup().regex_replace(r'=([0-9]+)>', r'="\1">'),
    'vendor/lib64/libcamximageformatutils.so': blob_fixup()
        .replace_needed('vendor.qti.hardware.display.config-V2-ndk_platform.so', 'vendor.qti.hardware.display.config-V2-ndk.so'),
    'vendor/lib64/libgf_hal.so': blob_fixup().binary_regex_replace(
        rb'\[%s\] openat: %s xiaomi_sysfs_fd,failed:\[fingerdown\]',
        b'[%s] openat: xiaomi_sysfs_fd,failed:[fingerdown]   ',
    ),
    (
        'vendor/lib64/hw/com.qti.chi.override.so',
        'vendor/lib64/libcamxcommonutils.so',
        'vendor/lib64/libmialgoengine.so',
    ): blob_fixup().add_needed('libprocessgroup_shim.so'),
}  # fmt: skip


module = ExtractUtilsModule(
    'marble',
    'xiaomi',
    blob_fixups=blob_fixups,
    lib_fixups=lib_fixups,
    namespace_imports=namespace_imports,
)

if __name__ == '__main__':
    utils = ExtractUtils.device_with_common(module, 'sm8450-common', module.vendor)
    utils.run()
