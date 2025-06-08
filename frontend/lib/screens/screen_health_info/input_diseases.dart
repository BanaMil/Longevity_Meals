// input_diseases.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:frontend/providers/health_info_provider.dart';
import 'package:frontend/widgets/selectable_button.dart';
import 'input_allergies.dart';

class InputDiseasesScreen extends StatelessWidget {
  const InputDiseasesScreen({super.key});

  final List<String> diseaseList = const [
    '간경변증',
    '갑상선 기능저하증',
    '갑상선 기능항진증',
    '고지혈증',
    '고혈압',
    '골다공증',
    '근감소증',
    '기관지천식',
    '뇌졸증',
    '뇌출혈',
    '당뇨병',
    '동맥경화증',
    '대장암',
    '만성 신부전',
    '만성 폐쇄성 폐질환',
    '말초신경병증',
    '부신기능저하증',
    '부정맥',
    '비만',
    '신증후군',
    '심근경색',
    '심부전',
    '요로결석',
    '요통',
    '유방암',
    '위식도역류질환',
    '위암',
    '위염',
    '저혈당',
    '전립선암',
    '지방간',
    '치매',
    '췌장염',
    '통풍',
    '퇴행성 관절염',
    '파킨슨병',
    '폐렴',
    '폐암',
    '협심증',
    '해당없음',
  ];

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<HealthInfoProvider>();
    return Scaffold(
      appBar: AppBar(
        title: const Text('질병 선택'),
        actions: [
          IconButton(
            icon: const Icon(Icons.arrow_forward),
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => const InputAllergiesScreen(),
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
            child: Text('질병을 선택해주세요.'),
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
                    diseaseList.map((d) {
                      final selected = provider.info.diseases.contains(d);
                      return SelectableButton(
                        text: d,
                        isSelected: selected,
                        onTap:
                            () => provider.toggleDiseaseAllergy(
                              d,
                              provider.info.diseases,
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
