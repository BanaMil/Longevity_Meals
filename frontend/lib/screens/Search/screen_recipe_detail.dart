// screen_recipe_detail.dart

import 'package:flutter/material.dart';
import 'package:frontend/models/food_item.dart';

class RecipeDetailScreen extends StatelessWidget {
  final FoodItem item;

  const RecipeDetailScreen({super.key, required this.item});

  @override
  Widget build(BuildContext context) {
    final bg = const Color.fromARGB(255, 235, 239, 165);
    final titleColor = const Color(0xFF2E2E2E);

    return Scaffold(
      backgroundColor: bg,
      appBar: AppBar(
        elevation: 0,
        backgroundColor: Colors.transparent,
        foregroundColor: Colors.black, 
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              ClipRRect(
                borderRadius: BorderRadius.circular(16),
                child: (item.imageUrl != null && item.imageUrl!.isNotEmpty)
                    ? Image.network(
                      item.imageUrl!,
                      height: 220,
                      fit: BoxFit.cover,
                    )
                    : Container(
                      height: 220,
                      color: Colors.grey[300],
                      alignment: Alignment.center,
                      child: const Icon(Icons.image_not_supported, size: 48),
                    ),
              ),
              const SizedBox(height: 12),

              // 음식명
              Text(
                item.name,
                textAlign: TextAlign.center,
                style: TextStyle(
                  fontSize: 26,
                  fontWeight: FontWeight.w900,
                  color: titleColor,
                ),
              ),
              const SizedBox(height: 16),

              // 재료
              _SectionCard(
                headerLeft: const Text(
                  '[ 재료 ]',
                  style: TextStyle(fontSize: 19, fontWeight: FontWeight.w800),
                ),
                headerRight: (item.servingCount != null)
                    ? Text(
                        '(${item.servingCount}인분 기준)',
                        style: const TextStyle(
                          fontSize: 14,
                          fontWeight: FontWeight.w600,
                          color: Colors.black54,
                        ),
                    )
                    : null,
                  child: _IngredientsList(ingredients: item.ingredients),
              ),
              const SizedBox(height: 12),


              // 만드는 방법
              _SectionCard(
                headerLeft: const Text(
                  '[ 만드는 방법 ]',
                  style: TextStyle(fontSize: 19, fontWeight: FontWeight.w800),
                ),
                child: _StepsList(steps: item.recipeSteps),
              ),
            ],
          ),
        ),
      ),
    );
  }
}


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
      padding: const EdgeInsets.fromLTRB(14, 12, 14, 12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(14),
        boxShadow: const [
          BoxShadow(color: Colors.black12, blurRadius: 4, offset: Offset(0,2))
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
          const SizedBox(height: 10),
          child,
        ],
      ),
    );
  }
}


/// 재료 리스트
class _IngredientsList extends StatelessWidget {
  final List<Map<String, String>>? ingredients;

  const _IngredientsList({required this.ingredients});

  @override
  Widget build(BuildContext context) {
    if (ingredients == null || ingredients!.isEmpty) {
      return const Text('재료 정보가 없습니다.',
          style: TextStyle(color: Colors.black54));
    }
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: ingredients!.map((m) {
        final name = (m['name'] ?? '').trim();
        final amount = (m['amount'] ?? '').trim();
        final line = amount.isNotEmpty ? '$name  $amount' : name;
        return Padding(
          padding: const EdgeInsets.symmetric(vertical: 3),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text('• ',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600)),
              Expanded(
                child: Text(
                  line,
                  style: const TextStyle(fontSize: 18),
                ),
              ),
            ],
          ),
        );
      }).toList(),
    );
  }
}

/// 만드는 방법 리스트
class _StepsList extends StatelessWidget {
  final List<String>? steps;

  const _StepsList({required this.steps});

  @override
  Widget build(BuildContext context) {
    if (steps == null || steps!.isEmpty) {
      return const Text('레시피 정보가 없습니다.',
          style: TextStyle(color: Colors.black54));
    }
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: List.generate(steps!.length, (i) {
        final step = steps![i];
        return Padding(
          padding: const EdgeInsets.symmetric(vertical: 4),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text('${i + 1}. ',
                  style: const TextStyle(
                      fontSize: 18, fontWeight: FontWeight.w700)),
              Expanded(
                child: Text(
                  step,
                  style: const TextStyle(fontSize: 18),
                ),
              ),
            ],
          ),
        );
      }),
    );
  }
}