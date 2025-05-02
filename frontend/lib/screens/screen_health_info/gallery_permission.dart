// gallery_permission.dart

import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'gallery.dart';

class GalleryPermissionScreen extends StatefulWidget {
  const GalleryPermissionScreen({super.key});

  @override
  State<GalleryPermissionScreen> createState() => _GalleryPermissionScreen();
}

class _GalleryPermissionScreen extends State<GalleryPermissionScreen> {
  Future<void> _requestPermission() async {
    final status = await Permission.photos.request(); // Android 13 이상은 READ_MEDIA_IMAGES 사용

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
            ElevatedButton(
              onPressed: _requestPermission, 
              child: const Text("허용"),
            ),
            OutlinedButton(
              onPressed: () => Navigator.pop(context), 
              child: const Text("허용안함"),
            ),
          ],
        ),
      ),
    );
  }
}