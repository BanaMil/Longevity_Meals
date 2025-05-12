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

  String getMealPeriodLabel() {
    final now = DateTime.now();
    final hour = now.hour;

    if( hour >= 0 && hour < 10) {
      return '아침밥상';
    } else if (hour >= 10 && hour < 16) {
      return '점심밥상';
    } else {
      return '저녁밥상';
    }
  }

  String getFormmattedDate() {
    final now = DateTime.now();
    return '${now.month}월 ${now.day}일';
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
          const Text('오늘의 밥상', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
          const SizedBox(height: 8),
          Text(
            '${getFormmattedDate()} ${getMealPeriodLabel()}',
            style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w500),
          ),
          const SizedBox(height: 12),
          Image.asset('assets/images/today_meal.png'), //식단 이미지

          const SizedBox(height: 20),

          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              Expanded(
                child: ElevatedButton(
                  onPressed: () {
                  /*
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (_) => const WeeklyMealScreen());
                  );
                  */

                  },
                  child: const Text('음식 정보 보기'),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: ElevatedButton(
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
              ),
            ],
          ),
        
          const SizedBox(width: 12),
        
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
