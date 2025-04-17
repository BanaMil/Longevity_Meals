// input_dislikes.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:frontend/providers/health_info_provider.dart';
import 'loading_health_info.dart';

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
                    // 로딩 화면
                    Navigator.of(context).push(
                      MaterialPageRoute(builder: (_) => const LoadingHealthInfoScreen()),
                    );

                    try {
                    // 백엔드에 전송
                      await context.read<HealthInfoProvider>().submitToServer();

                    // 전송 후 로딩 화면 닫고 성공 메시지
                      if (context.mounted) {
                        Navigator.of(context).pop(); //로딩 화면 pop
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text('건강정보가 저장되었습니다.')),
                        );                  
                      }
                    } catch (e) {
                      // 에러 시 로딩 화면 닫고 에러 메시지
                      if (context.mounted) {
                        Navigator.of(context).pop();
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