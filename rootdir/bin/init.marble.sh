#!/vendor/bin/sh
#
# Copyright (C) 2024 Paranoid Android
# SPDX-License-Identifier: Apache-2.0
#

# Set panel name sysprop
panel_node="/sys/class/mi_display/disp-DSI-0/panel_info"
if [ -f "$panel_node" ]; then
	panel_name="$(cat $panel_node | cut -c 12-)"
	setprop "ro.vendor.display.panel_name" "$panel_name"
fi
