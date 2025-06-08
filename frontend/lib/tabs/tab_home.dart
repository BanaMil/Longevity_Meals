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

  @override
  void initState() {
    super.initState();
    _loadUsername();
    _loadTodayMeal();
  }

  void _loadUsername() async {
    final userInfo = await UserStorage.loadUserInfo();
    debugPrint("📦 로드된 유저 정보: $userInfo");

    setState(() {
      username = userInfo['username'] ?? '사용자';
    });
  }

  void _loadTodayMeal() async {
    final userInfo = await UserStorage.loadUserInfo();
    final userId = userInfo['userid'];

    if (userId != null && userId.isNotEmpty) {
      try {
        final meal = await MealService.fetchTodayMeal(userId);
        setState(() {
          userid = userId;
          todayMeal = meal;
        });
      } catch (e) {
        debugPrint('오늘의 밥상 불러오기 실패: $e');
      }
    } else {
      debugPrint('사용자 ID 없음: 다시 로그인 필요');
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

  String getFormmattedDate() {
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
                      horizontal: 12,
                      vertical: 8,
                    ),
                    decoration: BoxDecoration(
                      color: const Color.fromARGB(255, 196, 215, 108),
                      border: Border.all(
                        color: const Color.fromARGB(255, 66, 105, 50),
                        width: 2,
                      ),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: const Text(
                      '장수밥상',
                      style: TextStyle(
                        fontSize: 18,
                        color: Color.fromARGB(255, 66, 105, 50),
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ),
                const SizedBox(height: 15),
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
                        /*
                      Navigator.push(
                        context, MaterialPageRoute(builder: (_) => const PointScreen()),
                      );
                      */
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

          // 흰색 컨테이너
          Expanded(
            child: Container(
              width: double.infinity,
              padding: const EdgeInsets.all(16),
              decoration: const BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.vertical(top: Radius.circular(30)),
              ),
              child: Scrollbar(
                thumbVisibility: false, // 항상 스크롤바 보이게 하려면 true
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
                        '${getFormmattedDate()} ${getMealPeriodLabel()}',
                        style: const TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                      const SizedBox(height: 12),

                      if (todayMeal == null)
                        const Center(child: Text('오늘의 밥상 정보 없음'))
                      else
                        Column(
                          children: [
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                              children: todayMeal!.sideDishes.map((dish) {
                                    return Column(
                                      children: [
                                        ClipRRect(
                                        child: Image.network(
                                          dish.imageUrl,
                                          width: 90,
                                          height: 90,
                                          fit: BoxFit.cover,
                                        ),
                                        ),
                                        /*
                                          Text(
                                            dish.name,
                                            style: const TextStyle(
                                              fontSize: 12,
                                            ),
                                          ),
                                          */
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
                                    child: Image.network(
                                      todayMeal!.rice.imageUrl,
                                      width: 130,
                                      height: 130,
                                      fit: BoxFit.cover,
                                    ),
                                    ),
                                    /*
                                      Text(
                                        meal.rice.name,
                                        style: const TextStyle(fontSize: 12),
                                      ),
                                      */
                                  ],
                                ),
                                Column(

                                  children: [
                                    ClipRRect(
                                    child: todayMeal!.soup != null
                                      ? Image.network(
                                        todayMeal!.soup!.imageUrl,
                                        width: 130,
                                        height: 130,
                                        fit: BoxFit.cover,
                                      )
                                    : const SizedBox(
                                        width: 130,
                                        height: 130,
                                      ),
                                    ),
                                     // soup없으면 빈 박스 표시                               
                                    /*
                                      Text(
                                        meal.soup.name,
                                        style: const TextStyle(fontSize: 12),
                                      ),
                                      */
                                  ],
                                ),
                              ],
                            ),
                            const Divider(thickness: 1),
                            const SizedBox(height: 10),
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                              children: [
                                Expanded(
                                  child: CustomButton(
                                    text: '음식 정보',
                                    icon: Icons.restaurant_menu,
                                    onPressed:
                                        todayMeal == null
                                            ? null // 데이터 없으면 비활성화
                                            : () {
                                              Navigator.push(
                                                context,
                                                MaterialPageRoute(
                                                  builder:
                                                      (_) => FoodInfoScreen(
                                                        meal: todayMeal!,
                                                      ),
                                                ),
                                              );
                                            },
                                  ),
                                ),
                                const SizedBox(width: 12),
                                Expanded(
                                  child: CustomButton(
                                    text: '일주일 밥상',
                                    icon: Icons.calendar_today,
                                    onPressed: () {
                                      if (userid == null || userid!.isEmpty) {
                                        showDialog(
                                          context: context,
                                          builder:
                                              (_) => const AlertDialog(
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
                                          builder:
                                              (_) => WeeklyMealsScreen(
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
                      const Text(
                        '오늘의 밥상 체크하기',
                        style: TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 10),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          SizedBox(
                            width: 160,
                            child: ElevatedButton.icon(
                              onPressed: () {
                                // 먹었습니다. 클릭 시
                              },
                              icon: const Icon(
                                Icons.circle_outlined,
                                color: Colors.black45,
                              ),
                              label: const Text(
                                '먹었습니다.',
                                style: TextStyle(color: Colors.black),
                              ),
                              style: ElevatedButton.styleFrom(
                                backgroundColor: Colors.blue[300],
                                minimumSize: const Size(120, 45), // 버튼 높이
                                padding: const EdgeInsets.symmetric(
                                  horizontal: 12,
                                  vertical: 12,
                                ),
                                textStyle: const TextStyle(
                                  fontSize: 15,
                                ), // 폰트 크기
                              ),
                            ),
                          ),

                          const SizedBox(width: 12),

                          SizedBox(
                            width: 160,
                            child: ElevatedButton.icon(
                              onPressed: () {
                                // 먹지 않았습니다. 클릭 시
                              },
                              icon: const Icon(
                                Icons.close,
                                color: Colors.black45,
                              ),
                              label: const Text(
                                '먹지 않았습니다.',
                                style: TextStyle(color: Colors.black),
                              ),
                              style: ElevatedButton.styleFrom(
                                backgroundColor: Colors.red[300],
                                minimumSize: const Size(120, 45), // 버튼 높이
                                padding: const EdgeInsets.symmetric(
                                  horizontal: 14,
                                  vertical: 14,
                                ),
                                textStyle: const TextStyle(
                                  fontSize: 15,
                                ), // 폰트 크기
                              ),
                            ),
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
