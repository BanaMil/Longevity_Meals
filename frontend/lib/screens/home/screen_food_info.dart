// screen_food_info.dart

import 'package:flutter/material.dart';
import 'package:frontend/models/meal.dart';
import 'package:frontend/models/food_item.dart';

class FoodInfoScreen extends StatelessWidget {
  final Meal meal;

  const FoodInfoScreen({super.key, required this.meal});

  @override
  Widget build(BuildContext context) {
    const bg = Color.fromARGB(255, 235, 239, 165);

    return Scaffold(
      backgroundColor: bg,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        foregroundColor: Colors.black,
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // 밥
              buildFoodSection(context, '🍚 밥', meal.rice),
              const SizedBox(height: 20),

              // 국
              if (meal.soup != null)
                buildFoodSection(context, '🥣 국', meal.soup!),
              if (meal.soup != null) const SizedBox(height: 24),

              // 반찬 헤더
              const Text(
                '🥗 반찬',
                style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 12),

              // 반찬들
              ...meal.sideDishes.map(
                (dish) => Padding(
                  padding: const EdgeInsets.only(bottom: 18),
                  child: buildFoodSection(context, null, dish),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget buildFoodSection(
      BuildContext context, String? categoryLabel, FoodItem food) {
    final titleColor = const Color(0xFF2E2E2E);

    /*
    debugPrint("🍴 [FoodInfoScreen] 음식명: ${food.name}");
    debugPrint("   영양소: ${food.nutrients}");
    debugPrint("   재료: ${food.ingredients}");
    debugPrint("   레시피: ${food.recipeSteps}");
    debugPrint("   인분: ${food.servingCount}");
    */    

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (categoryLabel != null) ...[
          Text(
            categoryLabel,
            style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 8),
        ],

        _SectionCard(
          headerLeft: Text(
            food.name,
            style: TextStyle(
              fontSize: 20,
              fontWeight: FontWeight.w900,
              color: titleColor,
            ),
          ),
          headerRight: (food.servingCount != null && food.servingCount! > 0)
              ? Text(
                  '(${food.servingCount}인분 기준)',
                  style: const TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                    color: Colors.black54,
                  ),
                )
              : const SizedBox.shrink(),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // 이미지
              ClipRRect(
                borderRadius: BorderRadius.circular(12),
                child: (food.imageUrl != null && food.imageUrl!.isNotEmpty)
                    ? Image.network(
                        food.imageUrl!,
                        height: 200,
                        width: double.infinity,
                        fit: BoxFit.cover,
                      )
                    : Container(
                        height: 200,
                        width: double.infinity,
                        color: Colors.grey[300],
                        alignment: Alignment.center,
                        child: const Text('사진 정보가 없습니다.'),
                      ),
              ),
              const SizedBox(height: 16),

              // [ 재료 ]
              const Text(
                '[ 재료 ]',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.w800),
              ),
              const SizedBox(height: 6),
              _IngredientsList(ingredients: food.ingredients),
              const SizedBox(height: 16),

              // [ 영양소 ] + 자세히 보기 Dialog
              const Text(
                '[ 영양소 ]',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.w800),
              ),
              const SizedBox(height: 6),
              if (food.nutrients != null && food.nutrients!.isNotEmpty)
                Align(
                  alignment: Alignment.centerLeft,
                  child: TextButton(
                    onPressed: () {
                      showDialog(
                        context: context,
                        builder: (context) {
                          return AlertDialog(
                            title: Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: const [
                                Text('영양소 정보'),
                                Text(
                                  '100g 기준',
                                  style: TextStyle(
                                      fontSize: 14, color: Colors.grey),
                                )
                              ],
                            ),
                            content: SingleChildScrollView(
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  ...food.nutrients!.map(
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
                                onPressed: () =>
                                    Navigator.of(context).pop(),
                                child: const Text('닫기'),
                              ),
                            ],
                          );
                        },
                      );
                    },
                    child: const Text('자세히 보기'),
                  ),
                )
              else
                const Padding(
                  padding: EdgeInsets.only(top: 4),
                  child: Text(
                    '영양소 정보가 없습니다.',
                    style: TextStyle(fontSize: 16, color: Colors.black54),
                  ),
                ),
              const SizedBox(height: 16),

              // [ 만드는 방법 ]
              const Text(
                '[ 만드는 방법 ]',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.w800),
              ),
              const SizedBox(height: 6),
              _StepsList(steps: food.recipeSteps),
            ],
          ),
        ),
      ],
    );
  }
}

/// 공통 카드
class _SectionCard extends StatelessWidget {
  final Widget headerLeft;
  final Widget? headerRight;
  final Widget child;

  const _SectionCard({
    required this.headerLeft,
    this.headerRight,
    required this.child,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.fromLTRB(14, 12, 14, 14),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(14),
        boxShadow: const [
          BoxShadow(
            color: Colors.black12,
            blurRadius: 4,
            offset: Offset(0, 2),
          )
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              headerLeft,
              const Spacer(),
              if (headerRight != null) headerRight!,
            ],
          ),
          const SizedBox(height: 12),
          child,
        ],
      ),
    );
  }
}

// 재료 리스트
class _IngredientsList extends StatelessWidget {
  final List<Map<String, dynamic>>? ingredients;

  const _IngredientsList({required this.ingredients});

  @override
  Widget build(BuildContext context) {
    if (ingredients == null || ingredients!.isEmpty) {
      return const Text(
        '재료 정보가 없습니다.',
        style: TextStyle(color: Colors.black54),
      );
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: ingredients!.map((m) {
        final name = (m['name'] ?? '').toString().trim();
        final amount = (m['amount'] ?? '').toString().trim();
        final line = amount.isNotEmpty ? '$name  $amount' : name;

        return Padding(
          padding: const EdgeInsets.symmetric(vertical: 3),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text('• ',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600)),
              Expanded(
                child: Text(
                  line,
                  style: const TextStyle(fontSize: 16),
                ),
              ),
            ],
          ),
        );
      }).toList(),
    );
  }
}

// 만드는 방법 리스트
class _StepsList extends StatelessWidget {
  final List<String>? steps;

  const _StepsList({required this.steps});

  @override
  Widget build(BuildContext context) {
    if (steps == null || steps!.isEmpty) {
      return const Text(
        '레시피 정보가 없습니다.',
        style: TextStyle(color: Colors.black54),
      );
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: List.generate(steps!.length, (i) {
        final step = steps![i];
        return Padding(
          padding: const EdgeInsets.symmetric(vertical: 3),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                '${i + 1}. ',
                style: const TextStyle(
                    fontSize: 16, fontWeight: FontWeight.w700),
              ),
              Expanded(
                child: Text(
                  step,
                  style: const TextStyle(fontSize: 16),
                ),
              ),
            ],
          ),
        );
      }),
    );
  }
}
