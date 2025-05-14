// gallery_permission.dart

import 'dart:io'; // Platform 구분을 위해 필요 
import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:frontend/widgets/custom_button.dart';
import 'gallery.dart';

class GalleryPermissionScreen extends StatefulWidget {
  const GalleryPermissionScreen({super.key});

  @override
  State<GalleryPermissionScreen> createState() => _GalleryPermissionScreen();
}

class _GalleryPermissionScreen extends State<GalleryPermissionScreen> {
  Future<void> _requestPermission() async {
    PermissionStatus status; 

    if(Platform.isAndroid) {
      // Android 13이상 권한 요청 (API 33+)
      status = await Permission.mediaLibrary.request();
      if (status.isDenied) {
        // Android 12 이하 대응
        status = await Permission.storage.request();
      }
    } else {
      status = PermissionStatus.denied;
    } 

    if (!mounted) return;

    if (status.isGranted) {
      Navigator.push(
        context,
        MaterialPageRoute(builder: (context) => const GalleryScreen()),
      );
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('갤러리 접근 권한이 필요합니다.')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text("장수밥상에서 사진을 선택하도록 허용하시겠습니까?"),
            const SizedBox(height: 20),
            CustomButton(
              text: '허용',
              onPressed: _requestPermission, 
            ),
            CustomButton(
              text: '허용안함',
              onPressed: () => Navigator.pop(context), 
            ),
          ],
        ),
      ),
    );
  }
}