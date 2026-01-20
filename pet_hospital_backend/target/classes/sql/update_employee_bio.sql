-- 为现有员工生成专业职业介绍
-- 注意：请根据实际员工数据调整

-- 示例：为医生角色生成介绍
UPDATE employees SET bio = '资深小动物内科医师，拥有8年临床经验。擅长猫狗常见病诊疗、传染病防治及营养管理。持有执业兽医师资格证书，曾在多家知名宠物医院担任主治医师，深受宠物主人信赖。' WHERE role = 'DOCTOR' AND name = '张医生';
UPDATE employees SET bio = '专业宠物外科专家，专攻骨科手术及软组织手术。在宠物急诊及重症监护方面拥有丰富经验，能够熟练处理各类复杂病例。致力于为每一位宠物提供安全、专业的医疗服务。' WHERE role = 'DOCTOR' AND name = '李医生';
UPDATE employees SET bio = '宠物皮肤病与眼科疾病专家，拥有硕士学位及多项专业资质。擅长皮肤病诊断治疗、眼科手术及慢性病管理。多次参加国内外学术交流，持续提升专业技能，为宠物健康保驾护航。' WHERE role = 'DOCTOR' AND name = '王医生';

-- 为前台角色生成介绍
UPDATE employees SET bio = '资深宠物医院前台接待，精通挂号流程、客户服务及医患沟通。以热情、专业的态度为每一位客户提供贴心服务，确保就诊流程顺畅高效。擅长处理紧急情况，是医院与客户之间的重要桥梁。' WHERE role = 'RECEPTIONIST';

-- 为管理员角色生成介绍  
UPDATE employees SET bio = '医院运营管理专家，负责医院日常运营、团队管理及质量控制。拥有丰富的医疗机构管理经验，致力于打造专业、高效的宠物医疗服务体系，为宠物及主人提供优质就医体验。' WHERE role = 'ADMIN';

-- 通用更新语句（如果上述具体姓名不匹配）
-- 为没有 bio 的医生生成默认介绍
UPDATE employees SET bio = '执业兽医师，具备扎实的专业知识和临床经验。擅长宠物常见病诊疗、健康体检及预防保健。秉持"用心呵护每一个小生命"的理念，为宠物提供专业、细致的医疗服务。' WHERE role = 'DOCTOR' AND (bio IS NULL OR bio = '');

-- 为没有 bio 的前台生成默认介绍
UPDATE employees SET bio = '专业宠物医院前台服务人员，熟悉医院业务流程，具备良好的沟通能力和服务意识。负责客户接待、挂号预约及咨询服务，用真诚和耐心为每一位客户提供优质体验。' WHERE role = 'RECEPTIONIST' AND (bio IS NULL OR bio = '');

-- 为没有 bio 的管理员生成默认介绍
UPDATE employees SET bio = '医院管理人员，负责协调医院各部门工作，确保医疗服务质量和运营效率。具备丰富的管理经验和专业素养，致力于为员工创造良好工作环境，为客户提供优质服务。' WHERE role = 'ADMIN' AND (bio IS NULL OR bio = '');
