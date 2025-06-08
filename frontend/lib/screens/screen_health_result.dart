// screen_health_result.dart

import 'package:flutter/material.dart';
import 'package:frontend/models/health_result.dart';
import 'package:frontend/services/service_health_result.dart';
import 'package:frontend/services/user_storage.dart';
// import 'package:frontend/screens/screen_index.dart';
import 'package:frontend/services/service_meal.dart';
import 'package:frontend/screens/home/screen_weekly_meals.dart';

class HealthResultScreen extends StatefulWidget {
  const HealthResultScreen({super.key});

  @override
  State<HealthResultScreen> createState() => _HealthResultScreenState();
}

class _HealthResultScreenState extends State<HealthResultScreen> {
  late Future<HealthResult>? analysisFuture;

  String? username;

  @override
  void initState() {
    super.initState();
    _loadUserIdAndFetch();
    _loadUsername();
  }

  void _loadUserIdAndFetch() async {
    final userInfo = await UserStorage.loadUserInfo();
    final userid = userInfo['userid'];
    if (userid != null && userid.isNotEmpty) {
      setState(() {
        analysisFuture = HealthResultService.fetchAnalysisResult(userid);
      });
    } else {
      setState(() {
        analysisFuture = Future.error('로그인 정보가 없습니다. \n다시 로그인 해주세요.');
      });
    }
  }

  void _loadUsername() async {
    final userInfo = await UserStorage.loadUserInfo();
    setState(() {
      username = userInfo['username'] ?? '사용자';
    });
  }

  @override
  Widget build(BuildContext context) {
    if (analysisFuture == null) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    return FutureBuilder<HealthResult>(
      future: analysisFuture,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Scaffold(
            body: Center(child: CircularProgressIndicator()),
          );
        } else if (snapshot.hasError) {
          return Scaffold(body: Center(child: Text("오류발생: ${snapshot.error}")));
        } else if (snapshot.hasData) {
          final data = snapshot.data!;
          return Scaffold(
            appBar: AppBar(title: const Text('건강정보'), centerTitle: true),
            body: SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  Row(
                    children: [
                      const Icon(Icons.arrow_back),
                      const SizedBox(width: 8),
                      Text(
                        "$username 님의 건강정보\n분석 결과입니다.",
                        style: const TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 24),
                  if (data.recommendedNutrients.isNotEmpty)
                    _InfoCard(
                      title: '권장영양소',
                      nutrients: data.recommendedNutrients,
                    ),
                  if (data.restrictedNutrients.isNotEmpty)
                    _InfoCard(
                      title: '제한영양소',
                      nutrients: data.restrictedNutrients,
                    ),
                  /*
                  if (data.cautionNutrients.isNotEmpty)
                    _InfoCard(
                      title: '주의해야할 영양소',
                      nutrients: data.cautionNutrients,
                    ),
                  */
                  if (data.personalizedIntake.isNotEmpty)
                    _InfoCard(
                      title: '개인 맞춤 영양소 섭취량',
                      nutrients:
                          data.personalizedIntake
                              .map(
                                (e) =>
                                    "${e.name}: ${e.amount.toStringAsFixed(1)}${e.unit}",
                              )
                              .toList(),
                    ),
                  const SizedBox(height: 5),

                  // 추천 음식, 비추천 음식 사진
                  /*
                  const Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      Text("추천 음식", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                      Text("비추천 음식", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                    ],
                  ),
                  const SizedBox(height: 12),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      _FoodImageBox(data.recommendedFood),
                      _FoodImageBox(data.notRecommendedFood),
                    ],
                  ),
                  */
                  Center(
                    /*
                      ElevatedButton(
                        onPressed: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (context) => const IndexScreen(),
                            ),
                          );
                        },
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color.fromARGB(
                            255,
                            196,
                            215,
                            110,
                          ),
                          minimumSize: const Size(150, 50), // 버튼 높이
                          textStyle: const TextStyle(fontSize: 20), // 폰트 크기
                        ),
                        child: Text(
                          '홈 화면으로 이동',
                          style: TextStyle(
                            color: Colors.black,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                      */
                    child: ElevatedButton(
                      onPressed: () async {
                        final userInfo = await UserStorage.loadUserInfo();
                        final userid = userInfo['userid'];

                        if (!mounted) return;

                        if (userid == null || userid.isEmpty) {
                          showDialog(
                            context: context,
                            builder:
                                (_) => const AlertDialog(
                                  title: Text("오류"),
                                  content: Text("사용자 정보가 없습니다."),
                                ),
                          );
                          return;
                        }
                        showDialog(
                          context: context,
                          barrierDismissible: false,
                          builder:
                              (context) => const AlertDialog(
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
                          await MealService.requestMealRecommendation(userid);

                          if (!mounted) return;
                          Navigator.pop(context); // AlertDialog 닫기

                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder:
                                  (context) =>
                                      WeeklyMealsScreen(userid: userid),
                            ),
                          );
                        } catch (e) {
                          if (!mounted) return;
                          Navigator.pop(context); // AlertDialog 닫기
                          showDialog(
                            context: context,
                            builder:
                                (_) => AlertDialog(
                                  title: const Text("오류"),
                                  content: Text(
                                    "식단 추천에 실패했습니다.\n${e.toString()}",
                                  ),
                                  actions: [
                                    TextButton(
                                      onPressed: () => Navigator.pop(context),
                                      child: const Text("확인"),
                                    ),
                                  ],
                                ),
                          );
                        }
                      },
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color.fromARGB(
                          255,
                          196,
                          215,
                          110,
                        ),
                        minimumSize: const Size(150, 50), // 버튼 높이
                        textStyle: const TextStyle(fontSize: 20), // 폰트 크기
                      ),
                      child: Text(
                        '식단 추천 받기',
                        style: TextStyle(
                          color: Colors.black,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ),
                ],
              ),

              // 버튼 추가
            ),
          );
        } else {
          return const Scaffold(body: Center(child: Text("데이터 없음")));
        }
      },
    );
  }
}

// 내부 위젯
class _InfoCard extends StatefulWidget {
  final String title;
  final List<String> nutrients;

  const _InfoCard({required this.title, required this.nutrients});

  @override
  State<_InfoCard> createState() => _InfoCardState();
}

class _InfoCardState extends State<_InfoCard> {
  bool showAll = false;

  @override
  Widget build(BuildContext context) {
    final List<String> summary = widget.nutrients.take(3).toList();

    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: Colors.green[100],
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            widget.title,
            style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 8),

          if (!showAll)
            Text(
              '${widget.title}는 ${summary.join(', ')} 등 입니다.',
              style: const TextStyle(fontSize: 18),
            )
          else
            ...widget.nutrients.map(
              (nutrient) => Padding(
                padding: const EdgeInsets.symmetric(vertical: 2),
                child: Text(
                  "• $nutrient",
                  style: const TextStyle(fontSize: 18),
                ),
              ),
            ),

          if (widget.nutrients.length > 3)
            Align(
              alignment: Alignment.centerRight,
              child: TextButton(
                onPressed: () => setState(() => showAll = !showAll),
                child: Text(showAll ? '닫기' : '더보기'),
              ),
            ),
        ],
      ),
    );
  }
}


/*
class _FoodImageBox extends StatelessWidget {
  final String imagePath;
  const _FoodImageBox(this.imagePath);

  @override
  Widget build(BuildContext context) {
    return ClipRRect(
      borderRadius: BorderRadius.circular(12),
      child: Image.asset(
        imagePath,
        width: 120,
        height: 100,
        fit: BoxFit.cover,
      ),
    );
  }
}
*/