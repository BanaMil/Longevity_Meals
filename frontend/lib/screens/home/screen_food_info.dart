// screen_food_info.dart

import 'package:flutter/material.dart';
import 'package:frontend/models/meal.dart';
import 'package:frontend/models/food_item.dart';

class FoodInfoScreen extends StatelessWidget {
  final Meal meal;

  const FoodInfoScreen({super.key, required this.meal});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color.fromARGB(255, 235, 239, 165),
      appBar: AppBar(
        backgroundColor: Colors.transparent,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            buildFoodSection(context, '🍚 밥', meal.rice),

            if (meal.soup != null)
              buildFoodSection(context, '🥣 국', meal.soup!),

            const Text(
              '🥗 반찬',
              style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 10),
            ...meal.sideDishes.map(
              (dish) => buildFoodSection(context, '', dish),
            ),
          ],
        ),
      ),
    );
  }

  Widget buildFoodSection(BuildContext context, String label, FoodItem food) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (label.isNotEmpty)
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 8),
            child: Text(label, style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
          ),
        ClipRRect(
          borderRadius: BorderRadius.circular(10),
          child: Image.network(
            food.imageUrl,
            fit: BoxFit.cover,
            height: 200,
            width: 200,
          ),
        ),
        const SizedBox(height: 10),
        Text(
          food.name,
          style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 10),
        const Text(
          '[영양소]',
          style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
        ),
        TextButton(
          onPressed: () {
            showDialog(
              context: context,
              builder: (context) {
                return AlertDialog(
                  title: const Text('영양소 정보'),
                  content: SingleChildScrollView(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        ...food.nutrients.map(
                          (nut) => Text(
                            '• ${nut.name} : ${nut.amount.toStringAsFixed(1)}${nut.unit}',
                            style: const TextStyle(fontSize: 16),
                          ),
                        ),
                      ],
                    ),
                  ),
                  actions: [
                    TextButton(
                      onPressed: () => Navigator.of(context).pop(),
                      child: const Text('닫기'),
                    ),
                  ],
                );
              },
            );
          },
          child: const Text('자세히 보기'),
        ),
        const SizedBox(height: 10),
        const Text(
          '[재료]',
          style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
        ),
        if (food.ingredients != null)
          ...food.ingredients!.map(
            (ing) => Text('• $ing', style: const TextStyle(fontSize: 16)),
          ),
        const SizedBox(height: 10),
        const Text(
          '[만드는 방법]',
          style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
        ),
        Text(
          food.recipe ?? '레시피 정보가 없습니다.',
          style: const TextStyle(fontSize: 16),
        ),
        const SizedBox(height: 24),
        const Divider(thickness: 1),
      ],
    );
  }
}
