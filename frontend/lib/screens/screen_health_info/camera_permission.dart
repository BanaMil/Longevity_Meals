// camera_permission.dart

import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:frontend/widgets/custom_button.dart';
import 'camera.dart';

class CameraPermissionScreen extends StatefulWidget {
  const CameraPermissionScreen({super.key});

  @override
  State<CameraPermissionScreen> createState() => _CameraPermissionScreenState();
}

class _CameraPermissionScreenState extends State<CameraPermissionScreen> {
  Future<void> _requestPermission() async {
    final status = await Permission.camera.request();
    if (!mounted) return;

    if (status.isGranted) {
      Navigator.push(
        context,
        MaterialPageRoute(builder: (context) => const CameraScreen()), //카메라 화면 생성하기
      );
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('카메라 권한이 필요합니다.')),
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
            const Text("장수밥상에서 사진을 촬영하도록 허용하시겠습니까?"),
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
