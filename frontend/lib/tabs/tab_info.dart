// tab_info.dart

import 'package:flutter/material.dart';
import 'package:frontend/screens/screen_health_result.dart';

class InfoTab extends StatelessWidget {
  const InfoTab({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('내정보'),
        centerTitle: true,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            const Divider(thickness: 1),

            const SizedBox(height: 16),
            _buildMenuItem(
              context,
              title: '건강정보 분석 결과',
              onTap: () {
                Navigator.push(context, MaterialPageRoute(builder: (_) => HealthResultScreen()));
              },
            ),
            const SizedBox(height: 12),
            _buildMenuItem(
              context,
              title: '계정 정보',
              onTap: () {
                // 계정 정보 화면 이동 
              },
            ),
            const SizedBox(height: 12),
            _buildMenuItem(
              context,
              title: '--설정',
              onTap: () {
                // 설정 페이지로 이동
              },
            ),

            const Spacer(),

            TextButton(
              onPressed: () {
                // 로그아웃 처리 로직 
              },
              child: const Text(
                '로그아웃',
                style: TextStyle(fontSize: 16, color: Colors.black),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildMenuItem(BuildContext context,
  {required String title, required VoidCallback onTap}) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: double.infinity,
        padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 20),
        decoration: BoxDecoration(
          color: Colors.grey[200],
          borderRadius: BorderRadius.circular(12),
        ),
        child: Text(
          title,
          style: const TextStyle(fontSize: 16),
        ),
      ),
    );
  }
}