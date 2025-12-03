// screen_delivery_meals.dart

import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:frontend/models/daily_meals.dart';
import 'package:frontend/models/food_with_intake.dart';
import 'package:frontend/services/service_delivery.dart';

class DeliveryMealsScreen extends StatelessWidget {
  final String userid;
  const DeliveryMealsScreen({super.key, required this.userid});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color.fromARGB(255, 235, 239, 165),
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        title: const Text(
          "배송 중인 식단",
          style: TextStyle(
            fontSize: 25,
            fontWeight: FontWeight.bold,
            color: Colors.black,
          ),
        ),
        centerTitle: true,
      ),
      body: FutureBuilder<Map<String, DailyMeals>>(
        future: DeliveryService.fetchInTransitMeals(userid),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('에러: ${snapshot.error}'));
          } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
            return const Center(child: Text('배송 중인 식단이 없습니다.'));
          }

          final mealsByDate = snapshot.data!;
          final sortedDates = mealsByDate.keys.toList()..sort();

          // 식단이 하나도 없는 날짜 필터링 
          final filteredDates =
              sortedDates.where((d) {
                final m = mealsByDate[d]!;
                return m.breakfast.isNotEmpty ||
                    m.lunch.isNotEmpty ||
                    m.dinner.isNotEmpty;
              }).toList();

          if (filteredDates.isEmpty) {
            return const Center(child: Text('배송 중인 식단이 없습니다.'));
          }

          return Padding(
            padding: const EdgeInsets.fromLTRB(16, 10, 16, 16),
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 25, vertical: 25),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(20),
              ),
              child: ListView.separated(
                itemCount: sortedDates.length,
                separatorBuilder: (_, __) => const SizedBox(height: 20),
                itemBuilder: (context, index) {
                  final dateKey = sortedDates[index];
                  final parsedDate = DateTime.parse(dateKey);
                  final formattedDate = DateFormat(
                    'M월 d일 (E)',
                    'ko_KR',
                  ).format(parsedDate);
                  final daily = mealsByDate[dateKey]!;
                  return _buildDateSection(formattedDate, daily);
                },
              ),
            ),
          );
        },
      ),
    );
  }

  Widget _buildDateSection(String formattedDate, DailyMeals daily) {
    final children = <Widget>[
      Center(child: Text(formattedDate, style: const TextStyle(fontSize: 20))),
      const SizedBox(height: 10),
    ];

    if (daily.breakfast.isNotEmpty) {
      children.add(_buildMealCard('아침밥상', daily.breakfast));
      children.add(const SizedBox(height: 10));
    }
    if (daily.lunch.isNotEmpty) {
      children.add(_buildMealCard('점심밥상', daily.lunch));
      children.add(const SizedBox(height: 10));
    }
    if (daily.dinner.isNotEmpty) {
      children.add(_buildMealCard('저녁밥상', daily.dinner));
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: children,
    );
  }

  Widget _buildMealCard(String title, List<FoodWithIntake> items) {
    return Container(
      margin: const EdgeInsets.symmetric(vertical: 6),
      padding: const EdgeInsets.all(8),
      decoration: BoxDecoration(
        border: Border.all(color: const Color.fromARGB(255, 66, 105, 50)),
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
                        (e) =>
                            Text(e.name, style: const TextStyle(fontSize: 16)),
                      )
                      .toList(),
            ),
          ),
        ],
      ),
    );
  }
}
