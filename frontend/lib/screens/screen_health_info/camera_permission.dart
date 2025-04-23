// camera_permission.dart

import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'camera.dart';

class CameraPermissionScreen extends StatelessWidget {
  const CameraPermissionScreen({super.key});

  Future<void> _requestPermission(BuildContext context) async {
    final status = await Permission.camera.request();
    if (status.isGrated) {
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
            const Text("장수밥상에서 사진을 촬영하고 동영상을 녹화하도록 허용하시겠습니까?"),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () => _requestPermission(context),
              child: const Text("허용"),
            ),
            OutlineButton(
              onPressed: () => Navigator.pop(context),
              child: const Text("허용안함"),
            ),
          ],
        ),
      ),
    );
  }  
}


