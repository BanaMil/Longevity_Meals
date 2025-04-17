// camera.dart
// OCR 추가하고 수정하기

import 'package:flutter/material.dart';
import 'choice_info.dart';

class CameraScreen extends StatelessWidget {
  const CameraScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Camera')),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.camera_alt, size: 100),
          const SizedBox(height: 20),
          ElevatedButton(
            onPressed: () {
              // 촬영 로직 대체
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => const ChoiceInfoScreen(),
                ),
              );
            },
            child: const Text("사진 촬영"),
          ),
        ],
      ),
    );
  }
}
