/*
 *  UVCCamera
 *  library and sample to access to UVC web camera on non-rooted Android device
 *
 * Copyright (c) 2014-2017 saki t_saki@serenegiant.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *  All files in the folder are under this Apache License, Version 2.0.
 *  Files in the libjpeg-turbo, libusb, libuvc, rapidjson folder
 *  may have a different license, see the respective files.
 */

package com.serenegiant.usb;

import android.util.SparseArray;

public class USBVendorId {
	private static final SparseArray<String> IDS = new SparseArray<String>();

	public static String vendorName(final int vendor_id) {
		return IDS.get(vendor_id);
	}

	static {

		IDS.put(8171, "RD 80 USB Camera");
		IDS.put(10006, "YUEN DA ELECTRONIC PRODUCTS FACTORY");
		IDS.put(10013, "Gionee Communication Equipment Co., Ltd. ShenZhen");
		IDS.put(10022, "Universal Electronics Inc. (dba: TVIEW)");
		IDS.put(1003, "Atmel Corporation");
		IDS.put(1006, "Mitsumi");
		IDS.put(1008, "HP Inc.");
		IDS.put(10112, "M31 Technology Corp.");
		IDS.put(10113, "Liteconn Co., Ltd.");
		IDS.put(10121, "Suzhou WEIJU Electronics Technology Co., Ltd.");
		IDS.put(10144, "Mondokey Limited");
		IDS.put(10149, "Advantest Corporation");
		IDS.put(10150, "iRobot Corporation");
		IDS.put(1020, "Elitegroup Computer Systems");
		IDS.put(1021, "Xilinx Inc.");
		IDS.put(10226, "Sibridge Tech.");
		IDS.put(1026, "ALi Corporation");
		IDS.put(1027, "Future Technology Devices International Limited");
		IDS.put(10275, "Dongguan Jiumutong Industry Co., Ltd.");
		IDS.put(10289, "Power Integrations");
		IDS.put(10291, "Oculus VR, Inc.");
		IDS.put(10300, "HIGH TEK HARNESS ENTERPRISE CO., LTD.");
		IDS.put(10316, "Full in Hope Co., Ltd.");
		IDS.put(1032, "Quanta Computer Inc.");
		IDS.put(10329, "Viconn Technology (HK) Co., Ltd.");
		IDS.put(1033, "NEC Corporation");
		IDS.put(1035, "Weltrend Semiconductor");
		IDS.put(1037, "VIA Technologies, Inc.");
		IDS.put(10374, "Seeed Technology Co., Ltd.");
		IDS.put(10375, "Specwerkz");
		IDS.put(1038, "MCCI Corporation");
		IDS.put(10398, "Esselte Leitz GmbH & Co. KG");
		IDS.put(10406, "E-SEEK Inc.");
		IDS.put(1041, "BUFFALO INC.");
		IDS.put(10423, "Pleora Technologies Inc.");
		IDS.put(10431, "Vitetech Int'l Co., Ltd.");
		IDS.put(1044, "Giga-Byte Technology Co., Ltd.");
		IDS.put(10446, "Changzhou Shi Wujin Miqi East Electronic Co., Ltd.");
		IDS.put(10457, "Shenzhen Ourconn Technology Co., Ltd.");
		IDS.put(10458, "G.SKILL Int'l Enterprice Co., Ltd.");
		IDS.put(1046, "Nuvoton Technology Corp.");
		IDS.put(10466, "Surplus Electronic Technology Co., Ltd.");
		IDS.put(10470, "BIAMP SYSTEMS");
		IDS.put(10509, "IBCONN Technologies (Shenzhen) Co., Ltd.");
		IDS.put(10510, "Fugoo Inc.");
		IDS.put(10519, "Pan Xin Precision Electronics Co., Ltd.");
		IDS.put(10530, "Dongguan Digi-in Digital Technology Co., Ltd.");
		IDS.put(1054, "Creative Labs");
		IDS.put(10540, "GENUSION, Inc.");
		IDS.put(10544, "Ineda Systems Inc.");
		IDS.put(10545, "Jolla Ltd.");
		IDS.put(10546, "Peraso Technologies, Inc.");
		IDS.put(10549, "Nanjing Magewell Electronics Co., Ltd.");
		IDS.put(10560, "Shenzhen Yiwanda Electronics Co., Ltd.");
		IDS.put(1057, "Nokia Corporation");
		IDS.put(10575, "Dollar Connection Ltd.");
		IDS.put(10595, "BIO-key International, Inc.");
		IDS.put(1060, "Microchip-SMSC");
		IDS.put(10603, "Xacti Corporation");
		IDS.put(10615, "Shenzhen Zowee Technology Co., Ltd.");
		IDS.put(10643, "ADPlaus Technology Limited");
		IDS.put(10646, "Unwired Technology");
		IDS.put(1065, "Cirrus Logic Inc.");
		IDS.put(10657, "Union Electric Plug & Connector Corp.");
		IDS.put(10674, "Canova Tech");
		IDS.put(10685, "Silicon Works");
		IDS.put(10695, "HANRICO ANFU ELECTRONICS CO., LTD.");
		IDS.put(10700, "Kodak Alaris");
		IDS.put(10702, "JGR Optics Inc.");
		IDS.put(10703, "Richtek Technology Corporation");
		IDS.put(10705, "Binatone Electronics Int. Ltd.");
		IDS.put(1071, "Molex Inc.");
		IDS.put(10715, "Shenzhen iBoard Technology Co., Ltd.");
		IDS.put(10719, "SMIT(HK) Limited");
		IDS.put(1072, "Fujitsu Component Limited");
		IDS.put(10725, "Dongguan Kechenda Electronic Technology Co., Ltd.");
		IDS.put(10726, "Fengshun Peiying Electro-Acoustic Co., Ltd.");
		IDS.put(10744, "MD ELEKTRONIK GmbH");
		IDS.put(10749, "Bad Elf, LLC");
		IDS.put(10770, "Vreo Limited");
		IDS.put(10772, "Kanex");
		IDS.put(10781, "Oxford Nanopore Technologies");
		IDS.put(10782, "Obsidian Technology");
		IDS.put(10783, "Lucent Trans Electronics Co., Ltd.");
		IDS.put(10784, "GUOGUANG GROUP CO., LTD.");
		IDS.put(10788, "CNPLUS");
		IDS.put(10789, "Fourstar Group");
		IDS.put(10790, "Tragant International Co., Ltd.");
		IDS.put(10791, "DongGuan LianGang Optoelectronic Technology Co., Ltd.");
		IDS.put(10797, "Atrust Computer Corp.");
		IDS.put(10798, "VIA Alliance Semiconductor Co., Ltd.");
		IDS.put(10799, "BSUN Electronics Co., Ltd.");
		IDS.put(1080, "Advanced Micro Devices");
		IDS.put(10807, "RTD Embedded Technologies, Inc.");
		IDS.put(10816, "Shenzhen Choseal Industrial Co., Ltd.");
		IDS.put(10817, "Canyon Semiconductor");
		IDS.put(10818, "Spectra7 Microsystems Corp.");
		IDS.put(10821, "Meizu Technology Co., Ltd.");
		IDS.put(10822, "Hubei Yingtong Telecommunication Cable Inc.");
		IDS.put(10829, "Wilder Technologies");
		IDS.put(10837, "Diodes Inc.");
		IDS.put(10846, "DuPont");
		IDS.put(1085, "Lexmark International Inc.");
		IDS.put(10852, "Zhejiang Songcheng Electronics Co., Ltd.");
		IDS.put(10859, "VSN Mobil");
		IDS.put(10875, "Bellwether Electronic Corp.");
		IDS.put(10878, "VAIO Corporation");
		IDS.put(10879, "Perixx Computer GmbH");
		IDS.put(10885, "HANK ELECTRONICS CO., LTD");
	}
}
