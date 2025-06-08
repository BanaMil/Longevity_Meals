// tab_info.dart

import 'package:flutter/material.dart';
import 'package:frontend/screens/screen_health_result.dart';
import 'package:frontend/screens/screen_health_info/choice_info.dart';
import 'package:frontend/screens/screen_first.dart';
import 'package:frontend/services/user_storage.dart';

class InfoTab extends StatefulWidget {
  const InfoTab({super.key});

  @override
  State<InfoTab> createState() => _InfoTabState();
}

class _InfoTabState extends State<InfoTab> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color.fromARGB(255, 235, 239, 165),
      // appBar: AppBar(title: const Text('내정보'), centerTitle: true,),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.fromLTRB(16, 10, 16, 20),
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 25, vertical: 25),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(20),
            ),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.start,
              children: [
                const Center(
                  child: Text(
                    '내정보',
                    style: TextStyle(
                      fontSize: 30,
                      fontWeight: FontWeight.bold,
                      color: Color.fromARGB(255, 66, 105, 50),
                    ),
                  ),
                ),
                const Divider(thickness: 1),

                const SizedBox(height: 16),
                _buildMenuItem(
                  context,
                  title: '건강정보 분석 결과',
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(builder: (_) => HealthResultScreen()),
                    );
                  },
                ),
                const SizedBox(height: 16),
                _buildMenuItem(
                  context,
                  title: '건강정보 다시 입력하기',
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(builder: (_) => ChoiceInfoScreen()),
                    );
                  },
                ),
                const SizedBox(height: 12),
                _buildMenuItem(
                  context,
                  title: '비밀번호 변경하기',
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(builder: (_) => HealthResultScreen()),
                    );
                  },
                ),
                const SizedBox(height: 12),
                _buildMenuItem(
                  context,
                  title: '설정',
                  onTap: () {
                    // 설정 페이지로 이동
                  },
                ),

                const Spacer(),

                TextButton(
                  onPressed: () {
                    showDialog(
                      context: context,
                      builder: (BuildContext dialogContext) {
                        return AlertDialog(
                          title: const Text('로그아웃 확인'),
                          content: const Text('로그아웃 하시겠습니까?'),
                          actions: [
                            TextButton(
                              onPressed: () async {
                                await UserStorage.clearUserInfo();

                                if (!context.mounted) return;

                                Navigator.of(dialogContext).pop();
                                Navigator.pushAndRemoveUntil(
                                  context,
                                  MaterialPageRoute(
                                    builder: (_) => const FirstScreen(),
                                  ),
                                  (route) => false, // 모든 route 제거
                                );
                              },
                              child: const Text('네'),
                            ),
                            TextButton(
                              onPressed: () {
                                Navigator.of(dialogContext).pop();
                              },
                              child: const Text('아니오'),
                            ),
                          ],
                        );
                      },
                    );
                  },
                  child: const Text(
                    '로그아웃',
                    style: TextStyle(fontSize: 16, color: Colors.black),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildMenuItem(
    BuildContext context, {
    required String title,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: double.infinity,
        padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 20),
        decoration: BoxDecoration(
          color: Colors.grey[200],
          borderRadius: BorderRadius.circular(12),
        ),
        child: Text(title, style: const TextStyle(fontSize: 16)),
      ),
    );
  }
}
