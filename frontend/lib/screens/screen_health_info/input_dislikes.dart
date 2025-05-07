// input_dislikes.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:frontend/providers/health_info_provider.dart';
import 'package:frontend/screens/screen_home.dart';

class InputDislikesScreen extends StatefulWidget {
  const InputDislikesScreen({super.key});

  @override
  State<InputDislikesScreen> createState() => _InputDislikesScreenState();
}

class _InputDislikesScreenState extends State<InputDislikesScreen> {
  final TextEditingController _controller = TextEditingController();

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<HealthInfoProvider>();
    final dislikes = provider.info.dislikes;

    return Scaffold(
      appBar: AppBar(title: const Text('비선호 음식 입력')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('비선호 음식을 입력하고 추가 버튼을 눌러주세요.'),
            Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _controller,
                    decoration: const InputDecoration(
                      hintText: '예시: 버섯, 브로콜리',
                    ),
                  ),
                ),
                IconButton(
                  icon: const Icon(Icons.add),
                  onPressed: () {
                    final text = _controller.text.trim();
                    if (text.isNotEmpty) {
                      provider.addDislike(text);
                      _controller.clear();
                    }
                  },
                )
              ],
            ),
            const SizedBox(height: 16),
            const Text('입력된 비선호 음식:'),
            Wrap(
              children: dislikes.map((item) => Chip(
                label: Text(item),
                deleteIcon: const Icon(Icons.close),
                onDeleted: () => provider.removeDislike(item),
              )).toList(),
            ),
            const Spacer(),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                IconButton(
                  icon: const Icon(Icons.arrow_back),
                  onPressed: () => Navigator.pop(context),
                ),
                IconButton(
                  icon: const Icon(Icons.check),
                  onPressed: () async {
                    showDialog(
                      context: context,
                      barrierDismissible: false,
                      builder: (_) => const AlertDialog(
                        content: Column(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            CircularProgressIndicator(),
                            SizedBox(height: 16),
                            Text("입력 받은 건강정보를\n분석하는 중입니다.\n잠시만 기다려주세요."),
                          ],
                        ),
                      )
                    );

                    try {
                      await context.read<HealthInfoProvider>().submitToServer();

                      if (context.mounted) {
                        Navigator.of(context).pop; // 로딩 다이얼로그 닫기

                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text('건강정보가 저장되었습니다.')),
                        );  
                        // 홈화면으로 이동
                        Navigator.of(context).pushReplacement(
                          MaterialPageRoute(builder: (_) => const HomeScreen()),
                        );
                      }
                    } catch (e) {
                      if (context.mounted) {
                        Navigator.of(context).pop(); // 로딩 다이얼로그 닫기
                        ScaffoldMessenger.of(context).showSnackBar(
                          SnackBar(content: Text('저장 중 오류 발생: $e')),
                        );
                      }
                     }
                  },
                ),
              ],
            ),
          ],
        ),
      ),
    );
  } 
}