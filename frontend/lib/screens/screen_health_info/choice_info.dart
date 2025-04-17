// choice_info.dart

import 'package:flutter/material.dart';
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
              "카메라로 건강진단서 정보를 입력하시겠습니까?\n더 정확한 분석을 할 수 있습니다",
              textAlign: TextAlign.center,
              style: TextStyle(fontSize: 16),
            ),
            const SizedBox(height: 30),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                ElevatedButton(
                  onPressed: () => Navigator.pop(context),
                  style: ElevatedButton.styleFrom(backgroundColor: Colors.blue),
                  child: const Text("O"),
                ),

                // X버튼 누르면 키, 몸무게 입력 창으로 이동
                ElevatedButton(
                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(builder: (_) => const InputHeightWeightScreen()),
                    );
                  },
                  style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
                  child: const Text("X"),
                ),
              ],
            )
          ],
        ),
      ),
    );  
  }
}

