// tab_home.dart

import 'package:flutter/material.dart';
import 'package:frontend/services/user_storage.dart';

class HomeTab extends StatefulWidget {
  const HomeTab({super.key});

  @override
  State<HomeTab> createState() => _HomeTabState();
}

class _HomeTabState extends State<HomeTab> {
  String? username;

  @override
  void initState() {
    super.initState();
    _loadUsername();
  }

  void _loadUsername() async {
    final userInfo = await UserStorage.loadUserInfo();
    setState(() {
      username = userInfo['username'] ?? '사용자';
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Text(
            '$username님 안녕하세요.',
            style: const TextStyle(fontSize: 18),
          ),
          const SizedBox(height: 8),
          const Text('오늘의 식단', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
          const SizedBox(height: 8),
          Image.asset('assets/images/today_meal.png'), //식단 이미지

          const SizedBox(height: 20),
          ElevatedButton(
            onPressed: () {
              /*
              Navigator.push(
                context,
                MaterialPageRoute(builder: (_) => const WeeklyMealScreen());
              );
              */
            },
            child: const Text('일주일 밥상'),
          ),
          ElevatedButton(
            onPressed: () {
              /*
              Navigator.push(
                context, 
                MaterialPageRoute(builder: (_) => const PointScreen()),
              );
              */
            },
            child: const Text('포인트 확인'),
          ),
        ],
      ),
    );
  }
}