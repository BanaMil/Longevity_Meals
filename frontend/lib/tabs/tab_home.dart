// tab_home.dart

import 'package:flutter/material.dart';
import 'package:frontend/services/user_storage.dart';
import 'package:frontend/services/service_meal.dart';
import 'package:frontend/models/meal.dart';
import 'package:frontend/widgets/custom_button.dart';
import 'package:frontend/screens/home/home.dart';

class HomeTab extends StatefulWidget {
  const HomeTab({super.key});

  @override
  State<HomeTab> createState() => _HomeTabState();
}

class _HomeTabState extends State<HomeTab> {
  String? username;
  String? userid;
  Meal? todayMeal;
  bool? healthInfoSubmitted;

  @override
  void initState() {
    super.initState();
    _loadUsername();
    _loadTodayMeal();
    _loadFlags();
  }

  Future<void> _loadFlags() async {
    final flag = await UserStorage.getHealthInfoSubmitted();

    if (!mounted) return;
    setState(() {
      healthInfoSubmitted = flag;
    });
  }

  void _loadUsername() async {
    final userInfo = await UserStorage.loadUserInfo();
    debugPrint("📦 로드된 유저 정보: $userInfo");

    setState(() {
      username = userInfo['username'] ?? '사용자';
    });
  }

  Future<void> _loadTodayMeal() async {
    final userInfo = await UserStorage.loadUserInfo();
    final userId = userInfo['userid'];

    if (userId == null || userId.isEmpty) {
      debugPrint('사용자 ID 없음: 다시 로그인 필요');
      return;
    }

    if (mounted) {
      setState(() {
        userid = userId;
      });
    }

    try {
      final meal = await MealService.fetchTodayMeal(userId);
      if (!mounted) return;
      setState(() {
        todayMeal = meal;
      });
    } catch (e) {
      debugPrint('오늘의 밥상 불러오기 실패: $e');
      if (!mounted) return;
      setState(() {
        todayMeal = null;
      });
    }
  }

  // 식단 생성하기 버튼: 일주일 밥상 생성 API 호출
  Future<void> _requestWeeklyRecommendation() async {
    if (userid == null || userid!.isEmpty) return;

    try {
      await _withProgress(() async {
        await MealService.requestMealRecommendation(userid!);
        return null;
      });

      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('일주일 밥상이 생성되었습니다.')),
      );
      await _loadTodayMeal(); 
    } catch (e) {
      if (!mounted) return;
      showDialog(
        context: context,
        builder: (_) => AlertDialog(
          title: const Text("오류"),
          content: Text("식단 추천에 실패했습니다.\n$e"),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text("확인"),
            ),
          ],
        ),
      );
    }
  }

  Future<T?> _withProgress<T>(Future<T> Function() task) async {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (_) => const AlertDialog(
        title: Text("식단 구성 중"),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            CircularProgressIndicator(),
            SizedBox(height: 16),
            Text("식단을 구성하고 있습니다.\n잠시만 기다려주세요."),
          ],
        ),
      ),
    );

    try {
      final result = await task();
      return result;
    } finally {
      if (mounted) Navigator.of(context, rootNavigator: true).pop();
    }
  }

  String getMealPeriodLabel() {
    final now = DateTime.now();
    final hour = now.hour;

    if (hour >= 0 && hour < 10) {
      return '아침밥상';
    } else if (hour >= 10 && hour < 16) {
      return '점심밥상';
    } else {
      return '저녁밥상';
    }
  }

  String getFormattedDate() {
    final now = DateTime.now();
    return '${now.month}월 ${now.day}일';
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color.fromARGB(255, 196, 215, 108),
      body: Column(
        children: [
          Container(
            width: double.infinity,
            padding: const EdgeInsets.fromLTRB(16, 30, 16, 20),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Center(
                  child: Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 10,
                      vertical: 8,
                    ),
                    decoration: BoxDecoration(
                      color: const Color.fromARGB(255, 196, 215, 108),
                      border: Border.all(
                        color: const Color.fromARGB(255, 100, 80, 47),
                        width: 2,
                      ),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Image.asset(
                      'assets/images/Logo.png',
                      height: 30,
                      fit: BoxFit.contain,
                    ),
                  ),
                ),
                const SizedBox(height: 12),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      "$username님 안녕하세요.",
                      style: const TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 10),
                    ElevatedButton(
                      onPressed: () {
                      },
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.white,
                        foregroundColor: Colors.orange,
                        side: const BorderSide(color: Colors.orange),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(10),
                        ),
                        padding: const EdgeInsets.symmetric(
                          horizontal: 12,
                          vertical: 8,
                        ),
                      ),
                      child: const Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Icon(Icons.monetization_on, color: Colors.orange),
                          SizedBox(width: 6),
                          Text(
                            '30,000',
                            style: TextStyle(color: Colors.orange),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),

          Expanded(
            child: Container(
              width: double.infinity,
              padding: const EdgeInsets.all(16),
              decoration: const BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.vertical(top: Radius.circular(30)),
              ),
              child: Scrollbar(
                thumbVisibility: false, 
                child: SingleChildScrollView(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        '오늘의 밥상',
                        style: TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        '${getFormattedDate()} ${getMealPeriodLabel()}',
                        style: const TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                      const SizedBox(height: 12),
                      if (todayMeal == null)
                        Column(
                          children: [
                            const SizedBox(height: 8),

                            // 오늘 식단이 없고, 건강정보가 제출 되었을 때
                            if (healthInfoSubmitted == true)
                              SizedBox(
                                width: double.infinity,
                                child: ElevatedButton.icon(
                                  onPressed: (userid == null || userid!.isEmpty)
                                      ? null
                                      : _requestWeeklyRecommendation,
                                  icon: const Icon(Icons.auto_awesome),
                                  label: const Text('식단 추천 받기'),
                                  style: ElevatedButton.styleFrom(
                                    minimumSize: const Size(0, 48),
                                    backgroundColor: Color.fromARGB(
                                      255,
                                      196,
                                      215,
                                      110,
                                    ),
                                    foregroundColor: Colors.black,
                                  ),
                                ),
                              ),

                            const SizedBox(height: 12),
                            const Center(child: Text('오늘의 밥상 정보 없음')),
                          ],
                        )
                      else
                        Column(
                          children: [
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                              children: todayMeal!.sideDishes.map((dish) {
                                return Column(
                                  children: [
                                    ClipRRect(
                                      borderRadius: BorderRadius.circular(
                                        8,
                                      ),
                                      child: (dish.imageUrl != null &&
                                              dish.imageUrl!.isNotEmpty)
                                          ? Image.network(
                                              dish.imageUrl!,
                                              width: 90,
                                              height: 90,
                                              fit: BoxFit.cover,
                                            )
                                          : Container(
                                              width: 90,
                                              height: 90,
                                              color: Colors.grey[300],
                                              child: const Center(
                                                child: Text(
                                                  '이미지 없음',
                                                  style: TextStyle(
                                                    fontSize: 12,
                                                  ),
                                                ),
                                              ),
                                            ),
                                    ),
                                  ],
                                );
                              }).toList(),
                            ),
                            const SizedBox(height: 10),
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                              children: [
                                Column(
                                  children: [
                                    ClipRRect(
                                      borderRadius: BorderRadius.circular(10),
                                      child:
                                          (todayMeal!.rice.imageUrl != null &&
                                                  todayMeal!.rice.imageUrl!
                                                      .isNotEmpty)
                                              ? Image.network(
                                                  todayMeal!.rice.imageUrl!,
                                                  width: 130,
                                                  height: 130,
                                                  fit: BoxFit.cover,
                                                )
                                              : Container(
                                                  width: 130,
                                                  height: 130,
                                                  color: Colors.grey[300],
                                                  child: const Center(
                                                    child: Text(
                                                      '이미지 없음',
                                                      style: TextStyle(
                                                        fontSize: 12,
                                                      ),
                                                    ),
                                                  ),
                                                ),
                                    ),
                                  ],
                                ),
                                Column(
                                  children: [
                                    ClipRRect(
                                      borderRadius: BorderRadius.circular(10),
                                      child: (todayMeal!.soup != null &&
                                              todayMeal!.soup!.imageUrl !=
                                                  null &&
                                              todayMeal!
                                                  .soup!.imageUrl!.isNotEmpty)
                                          ? Image.network(
                                              todayMeal!.soup!.imageUrl!,
                                              width: 130,
                                              height: 130,
                                              fit: BoxFit.cover,
                                            )
                                          : Container(
                                              width: 130,
                                              height: 130,
                                              color: Colors.grey[300],
                                              child: const Center(
                                                child: Text(
                                                  '이미지 없음',
                                                  style: TextStyle(
                                                    fontSize: 12,
                                                  ),
                                                ),
                                              ),
                                            ),
                                    ),
                                  ],
                                ),
                              ],
                            ),
                            const SizedBox(height: 8),
                            const Divider(thickness: 1),
                            const SizedBox(height: 10),
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                              children: [
                                Expanded(
                                  child: CustomButton(
                                    text: '음식 정보',
                                    icon: Icons.restaurant_menu,
                                    onPressed: todayMeal == null
                                        ? null
                                        : () {
                                            Navigator.push(
                                              context,
                                              MaterialPageRoute(
                                                builder: (_) => FoodInfoScreen(
                                                  meal: todayMeal!,
                                                ),
                                              ),
                                            );
                                          },
                                  ),
                                ),
                                const SizedBox(width: 6),
                                Expanded(
                                  child: CustomButton(
                                    text: '일주일 밥상',
                                    icon: Icons.calendar_today,
                                    onPressed: () {
                                      if (userid == null || userid!.isEmpty) {
                                        showDialog(
                                          context: context,
                                          builder: (_) => const AlertDialog(
                                            title: Text("오류"),
                                            content: Text(
                                              "사용자 정보를 불러올 수 없습니다.",
                                            ),
                                          ),
                                        );
                                        return;
                                      }

                                      Navigator.push(
                                        context,
                                        MaterialPageRoute(
                                          builder: (_) => WeeklyMealsScreen(
                                            userid: userid!,
                                          ),
                                        ),
                                      );
                                    },
                                  ),
                                ),
                              ],
                            ),
                          ],
                        ),
                      const SizedBox(height: 6),
                      const Divider(thickness: 1),
                      Center(
                        child: CustomButton(
                          text: '밥상 기록',
                          icon: Icons.border_color,
                          onPressed: () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (_) => const MealsRecordScreen(),
                              ),
                            );
                          },
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
