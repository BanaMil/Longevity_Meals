// input_allergies.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:frontend/providers/health_info_provider.dart';
import 'package:frontend/widgets/selectable_button.dart';
import 'input_dislikes.dart';

class InputAllergiesScreen extends StatelessWidget {
  const InputAllergiesScreen({super.key});

  final List<String> allergyList = const [
    '난류',
    '우유',
    '메밀',
    '땅콩',
    '대두',
    '밀',
    '잣',
    '호두',
    '게',
    '새우',
    '오징어',
    '고등어',
    '조개류',
    '복숭아',
    '토마토',
    '닭고기',
    '돼지고기',
    '소고기',
    '아황산류',
    '해당없음',
  ];

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<HealthInfoProvider>();
    return Scaffold(
      appBar: AppBar(
        title: const Text('알레르기 선택'),
        actions: [
          IconButton(
            icon: const Icon(Icons.arrow_forward),
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => const InputDislikesScreen(),
                ),
              );
            },
          ),
        ],
      ),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Padding(
            padding: EdgeInsets.all(16),
            child: Text('가지고 있는 알레르기가 있다면 선택해주세요.'),
          ),

          Expanded(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: GridView.count(
                crossAxisCount: 3, // 3열 고정
                mainAxisSpacing: 12, // 세로 간격
                crossAxisSpacing: 8, //가로 간격
                childAspectRatio: 2.8, // 버튼 가로/세로 비율 조정
                children:
                    allergyList.map((a) {
                      final selected = provider.info.allergies.contains(a);
                      return SelectableButton(
                        text: a,
                        isSelected: selected,
                        onTap:
                            () => provider.toggleDiseaseAllergy(
                              a,
                              provider.info.allergies,
                            ),
                      );
                    }).toList(),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
