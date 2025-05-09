// input_diseases.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:frontend/providers/health_info_provider.dart';
import 'package:frontend/widgets/selectable_button.dart';
import 'input_allergies.dart';

class InputDiseasesScreen extends StatelessWidget {
    const InputDiseasesScreen({super.key});

    final List<String> diseaseList = const [
        '고혈압', '심부전', '협심증', '심근경색',
        '부정맥', '동맥경화증', '당뇨병', '저혈당',
        '고지혈증', '비만', '통풍', '만성 신부전',
        '신증후군', '요로결석', '위염', '위식도역류질환',
        '간경변증', '지방간', '췌장염', '골다공증',
        '퇴행성 관절염', '요통', '근감소증', '치매',
        '파킨슨병', '뇌졸증', '뇌출혈', '말초신경병증',
        '만성 폐쇄성 폐질환', '기관지천식', '폐렴', '갑상선 기능저하증',
        '갑상선 기능항진증', '부신기능저하증', '위암', '대장암',
        '폐암', '전립선암', '유방암', '해당없음',
    ];

    @override
    Widget build(BuildContext context) {
        final provider = context.watch<HealthInfoProvider>();
        return Scaffold(
            appBar: AppBar(title: const Text('질병 선택')),
            body: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                    const Padding(
                        padding: EdgeInsets.all(16),
                        child: Text('질병을 선택해주세요.'),
                    ),
                    Wrap(
                        children: diseaseList.map((d) {
                            final selected = provider.info.diseases.contains(d);
                            return SelectableButton(
                                text: d,
                                isSelected: selected,
                                onTap: () => provider.toggleDiseaseAllergy(d, provider.info.diseases),
                            );
                        }).toList(),
                    ),
                    const Spacer(),
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
                                    MaterialPageRoute(builder: (context) => const InputAllergiesScreen()), //카메라 화면 생성하기
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