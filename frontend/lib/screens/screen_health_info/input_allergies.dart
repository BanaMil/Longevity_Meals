// input_allergies.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:frontend/providers/health_info_provider.dart';
import 'package:frontend/widgets/selectable_button.dart';
import 'input_dislikes.dart';

class InputAllergiesScreen extends StatelessWidget {
  const InputAllergiesScreen({super.key});

  final List<String> allergyList = const [
        '난류', '우유', '메밀', '땅콩', 
        '대두', '밀', '잣', '호두', 
        '게', '새우', '오징어', '고등어',
        '조개류', '복숭아', '토마토', '닭고기',
        '돼지고기', '소고기', '아황산류', '해당없음',
  ];

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<HealthInfoProvider>();
    return Scaffold(
      appBar: AppBar(title: const Text('알레르기 선택')),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Padding(
            padding: EdgeInsets.all(16),
            child: Text('가지고 있는 알레르기가 있다면 선택해주세요.'),
          ),

          Expanded(
            child:  SingleChildScrollView(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Wrap(
                alignment: WrapAlignment.center,
                spacing: 8,
                runSpacing: 8,
                children: allergyList.map((a) {
                  final selected = provider.info.allergies.contains(a);
                  return SelectableButton(
                    text: a,
                    isSelected: selected,
                    onTap: () => provider.toggleDiseaseAllergy(a, provider.info.allergies),
                  );
                }).toList(),
              ),
            ),
          ),
          
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              IconButton(
                icon: const Icon(Icons.arrow_back),
                onPressed: () => Navigator.pop(context),
              ),
              IconButton(
                icon: const Icon(Icons.arrow_forward),
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => const InputDislikesScreen()), //카메라 화면 생성하기
                  );
                }, 
              ),    
            ],
          )
        ],
      ),
    );
  }
}