// screen_weekly_meals.dart

import 'package:flutter/material.dart';
import 'package:intl/intl.dart'; // 날짜 포맷용
import 'package:frontend/models/daily_meals.dart';
import 'package:frontend/services/service_meal.dart';
import 'package:frontend/screens/screen_index.dart';
import 'package:frontend/models/food_with_intake.dart';
import 'package:frontend/screens/Delivery/screen_delivery_request.dart';

class WeeklyMealsScreen extends StatelessWidget {
  final String userid;
  const WeeklyMealsScreen({super.key, required this.userid});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color.fromARGB(255, 235, 239, 165),
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        title: const Text(
          '일주일 밥상',
          style: TextStyle(
            fontSize: 25,
            fontWeight: FontWeight.bold,
            color: Colors.black,
          ),
        ),
        centerTitle: true,
      ),

      body: FutureBuilder<Map<String, DailyMeals>>(
        future: MealService.fetchWeeklyMeals(userid),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('에러: ${snapshot.error}'));
          } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
            return const Center(child: Text('식단 정보가 없습니다.'));
          }

          final mealsByDate = snapshot.data!;
          final sortedDates = mealsByDate.keys.toList()..sort();

          return Padding(
            padding: const EdgeInsets.fromLTRB(16, 10, 16, 16),
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 25, vertical: 25),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(20),
              ),
              child: Column(
                children: [
                  Expanded(
                    child: ListView.separated(
                      itemCount: sortedDates.length,
                      separatorBuilder:
                          (context, index) => const SizedBox(height: 20),
                      itemBuilder: (context, index) {
                        final dateKey = sortedDates[index];
                        final parsedDate = DateTime.parse(dateKey);
                        final formattedDate = DateFormat(
                          'M월 d일 (E)',
                          'ko_KR',
                        ).format(parsedDate); // ex) 5월 21일 (화)
                        final DailyMeals dailyMeals = mealsByDate[dateKey]!;
                        return _buildDateSection(formattedDate, dailyMeals);
                      },
                    ),
                  ),
                  const SizedBox(height: 20),

                  // 배송 요청 버튼
                  Row(
                    children: [
                      Expanded(
                        child: ElevatedButton(
                          onPressed: () => _showDeliveryConfirm(context, mealsByDate),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: const Color.fromARGB(255, 196, 215, 110),
                            minimumSize: const Size(120, 50),
                            textStyle: const TextStyle(fontSize: 18),
                          ),
                          child: const Text(
                            '배송 요청',
                            style: TextStyle(
                              color: Colors.black,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: ElevatedButton(
                          onPressed: () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (context) => IndexScreen(userid: userid),
                              ),
                            );
                          },
                          style: ElevatedButton.styleFrom(
                            backgroundColor: const Color.fromARGB(255, 196, 215, 110),
                            minimumSize: const Size(120, 50), 
                            textStyle: const TextStyle(fontSize: 18),
                          ),
                          child: Text(
                            '홈 화면으로 이동',
                            style: TextStyle(
                            color: Colors.black,
                            fontWeight: FontWeight.bold,
                            fontSize: 14,
                            ),
                          ),
                        ), 
                      ),
                    ],
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }


  Future<void> _showDeliveryConfirm(
    BuildContext context,
    Map<String, DailyMeals> mealsByDate,
  ) async {
    final ok = await showDialog<bool> (
      context: context,
      barrierDismissible: false,
      builder: (context) {
        return AlertDialog(
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
          title: const Text('배송 요청'),
          content: const Text('식단 배송을 요청하시겠습니까?'),
          actionsAlignment: MainAxisAlignment.center,
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context, false),
              style: TextButton.styleFrom(
                    backgroundColor: Colors.red[300],
                    minimumSize: const Size(60, 40),
                  ),
              child: const Text('X'),
            ),
            TextButton(
              onPressed: () => Navigator.pop(context, true),
              style: TextButton.styleFrom(
                    backgroundColor: Colors.blue[300],
                    minimumSize: const Size(60, 40),
              ),
              child: const Text('O'),
            ),
          ],
        );
      },
    );

    if (!context.mounted) return;
    if (ok ==true) {
      Navigator.push(
        context,
        MaterialPageRoute(
          builder: (_) => DeliveryRequestScreen(
            userid: userid,
            mealsByDate: mealsByDate,
          ),
        ),
      );
    }
  }


  Widget _buildDateSection(String formattedDate, DailyMeals dailymeals) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Center(
          child: Text(formattedDate, style: const TextStyle(fontSize: 20)),
        ),
        const SizedBox(height: 10),
        _buildMealCard('아침밥상', dailymeals.breakfast),
        _buildMealCard('점심밥상', dailymeals.lunch),
        _buildMealCard('저녁밥상', dailymeals.dinner),
      ],
    );
  }

  Widget _buildMealCard(String title, List<FoodWithIntake> items) {
    return Container(
      margin: const EdgeInsets.symmetric(vertical: 6),
      padding: const EdgeInsets.all(8),
      decoration: BoxDecoration(
        border: Border.all(color: Color.fromARGB(255, 66, 105, 50)),
        borderRadius: BorderRadius.circular(8),
      ),
      child: Row(
        children: [
          SizedBox(
            width: 80,
            child: Text(
              title,
              style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              textAlign: TextAlign.center,
            ),
          ),
          const SizedBox(width: 8),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children:
                  items
                      .map(
                        (item) => Text(
                          item.name,
                          style: const TextStyle(fontSize: 16),
                        ),
                      )
                      .toList(),
            ),
          ),
        ],
      ),
    );
  }
}
