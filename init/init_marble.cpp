/*
 * Copyright (C) 2021 The LineageOS Project
 *           (C) 2024 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#include <libinit_variant.h>

#include "vendor_init.h"

static const variant_info_t marble = {
    .hwc = "CN",
    .brand = "Redmi",
    .device = "marble",
    .model = "23049RAD8C",
    .name = "marble",
    .mod_device = "marble",
    .marketname = "Redmi Note 12 Turbo",
};

static const variant_info_t marble_global = {
    .hwc = "GL",
    .brand = "POCO",
    .device = "marble",
    .model = "23049PCD8G",
    .name = "marble_global",
    .mod_device = "marble_global",
    .marketname = "POCO F5",
};

static const variant_info_t marble_in = {
    .hwc = "IN",
    .brand = "POCO",
    .device = "marblein",
    .model = "23049PCD8I",
    .name = "marblein",
    .mod_device = "marble_in_global",
    .marketname = "POCO F5",
};

static const std::vector<variant_info_t> variants = {
    marble,
    marble_global,
    marble_in,
};

void vendor_load_properties() {
    search_set_variant_props(variants);
}
