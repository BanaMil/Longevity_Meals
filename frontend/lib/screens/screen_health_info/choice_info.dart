// choice_info.dart

import 'package:flutter/material.dart';
import 'package:frontend/widgets/custom_button.dart';
import 'gallery_permission.dart';
import 'camera_permission.dart';
import 'input_height_weight.dart';


class ChoiceInfoScreen extends StatelessWidget {
  const ChoiceInfoScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("건강정보 입력")),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text(
              "카메라로 건강진단서 정보를 입력하시겠습니까?\n더 정확한 분석을 할 수 있습니다.",
              textAlign: TextAlign.center,
              style: TextStyle(fontSize: 25),
            ),
            const SizedBox(height: 40),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                // o버튼을 누르면 카메라 or 갤러리 선택 다이얼로그 띄우기 
                ElevatedButton(
                  onPressed: () => showImageSourceDialog(context),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.blue[300],
                    minimumSize: const Size(100, 70)),
                  child: const Text(
                    "O",
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 30,
                      color: Colors.black,
                    ),
                  ), 
                ),
                // X버튼 누르면 키, 몸무게 입력 창으로 이동
                ElevatedButton(
                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(builder: (_) => const InputHeightWeightScreen()),
                    );
                  },
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.red[300],
                    minimumSize: const Size(100, 70)),
                  child: const Text(
                    "X",
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 30,
                      color: Colors.black,
                    ),
                  ),
                ),
              ],
            )
          ],
        ),
      ),
    );  
  }
}


// 카메라 or 갤러리 선택 다이얼로그 함수
void showImageSourceDialog(BuildContext context) {
  showDialog(
    context: context,
    builder: (_) => AlertDialog(
      title: const Text('건강정보 입력'),
      content: const Text('건강진단서를 올릴 방법을 선택해주세요.😊'),  
      actions: [
        CustomButton(
          text: '카메라로 촬영하기',
          onPressed: () {
            Navigator.of(context).pop();
            Navigator.push(
              context,
              MaterialPageRoute(builder: (_) => const CameraPermissionScreen()),
            );
          },
        ),
        CustomButton(
          text: '갤러리에서 가져오기',
          onPressed: () {
            Navigator.of(context).pop();
            Navigator.push(
              context,
              MaterialPageRoute(builder: (_) => const GalleryPermissionScreen()),
            );
          },
        ),
      ],
    ),
  );
}