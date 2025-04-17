// loading_health_info.dart

import 'package:flutter/material.dart';

class LoadingHealthInfoScreen extends StatelessWidget {
  const LoadingHealthInfoScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      body: Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                '감사합니다.',
                style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
              ),
              SizedBox(height: 12),
              Text(
                '입력 받은 건강정보를\n분석하는 중입니다.\n잠시만 기다려주세요.',
                textAlign: TextAlign.center,
              ),
              SizedBox(height: 32),
              CircularProgressIndicator(),
            ],
          ),
        ),
      ),
    );
  }
}