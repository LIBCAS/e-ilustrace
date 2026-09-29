package cz.inqool.eas.eil.init;

import cz.inqool.eas.common.init.DatedInitializer;
import cz.inqool.eas.eil.iconclass.IconclassCategory;
import cz.inqool.eas.eil.iconclass.IconclassRepository;
import cz.inqool.eas.eil.iconclass.IconclassService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ConditionalOnProperty(prefix = "eil.init.demo", name = "enabled", havingValue = "true")
@Order(10)
@Component
@Slf4j
public class IconclassCategotyInitializer extends DatedInitializer<IconclassCategory, IconclassRepository> {
    public static final String ICONCLASS_ID_1 = "12acfb67-4096-41a6-9f23-49c6d6b360be";
    public static final String ICONCLASS_CODE_1 = "1";
    public static final String ICONCLASS_ID_2 = "b4e4daa7-2221-4e59-8053-c39eafbdd137";
    public static final String ICONCLASS_CODE_2 = "2";
    public static final String ICONCLASS_ID_3 = "a21d096f-0472-4e30-81d2-3a1eded16f36";
    public static final String ICONCLASS_CODE_3 = "3";
    public static final String ICONCLASS_ID_4 = "03ee5be8-88fa-4e57-9033-762b4a62b94b";
    public static final String ICONCLASS_CODE_4 = "4";
    public static final String ICONCLASS_ID_5 = "5d8ae56c-1506-46cc-b5ea-4003bf64ed17";
    public static final String ICONCLASS_CODE_5 = "5";
    public static final String ICONCLASS_ID_6 = "7c547365-6cae-4ec1-8f82-05428d2f497c";
    public static final String ICONCLASS_CODE_6 = "6";
    public static final String ICONCLASS_ID_7 = "37e2257f-089d-4330-a75d-10cc74cd43ed";
    public static final String ICONCLASS_CODE_7 = "7";
    public static final String ICONCLASS_ID_8 = "df3034ac-87dc-4176-af32-3f6579c9b9a1";
    public static final String ICONCLASS_CODE_8 = "8";
    public static final String ICONCLASS_ID_9 = "ac69c67f-4911-4c23-8f61-dd3f0decfdaf";
    public static final String ICONCLASS_CODE_9 = "9";
    public static final String ICONCLASS_ID_10 = "e4eace2e-494a-4d88-a40d-14f0691f1fac";
    public static final String ICONCLASS_CODE_10 = "10";
    public static final String ICONCLASS_ID_11 = "1ff15b5e-4778-4344-97d2-0f5312f7df99";
    public static final String ICONCLASS_CODE_11 = "11";
    public static final String ICONCLASS_ID_12 = "49a71059-ea74-461a-9a23-6bcd4db22315";
    public static final String ICONCLASS_CODE_12 = "12";
    public static final String ICONCLASS_ID_13 = "4aaf2ba2-24ed-4ee7-959c-97809d5269c0";
    public static final String ICONCLASS_CODE_13 = "13";
    public static final String ICONCLASS_ID_14 = "1fec0334-c039-444d-bbc4-118a10b7d712";
    public static final String ICONCLASS_CODE_14 = "14";
    public static final String ICONCLASS_ID_20 = "e7da2f5f-87ef-473f-9e54-6d85e7591a37";
    public static final String ICONCLASS_CODE_20 = "20";
    public static final String ICONCLASS_ID_21 = "78e62879-bb23-495e-b120-48e010dac884";
    public static final String ICONCLASS_CODE_21 = "21";
    public static final String ICONCLASS_ID_22 = "b2b96382-9750-4cf4-8e8c-8f6d818bfc07";
    public static final String ICONCLASS_CODE_22 = "22";
    public static final String ICONCLASS_ID_23 = "e20a4b8f-4b32-46c8-9323-7eca7a86b683";
    public static final String ICONCLASS_CODE_23 = "23";
    public static final String ICONCLASS_ID_24 = "3f7446a8-bbea-4a99-a181-a0e7bfaa5b40";
    public static final String ICONCLASS_CODE_24 = "24";
    public static final String ICONCLASS_ID_25 = "b666b2da-5bb9-46df-8c8b-8e2b87617204";
    public static final String ICONCLASS_CODE_25 = "25";
    public static final String ICONCLASS_ID_26 = "f6699201-4b8f-4d90-baf2-af8f4fd9ce10";
    public static final String ICONCLASS_CODE_26 = "26";
    public static final String ICONCLASS_ID_29 = "891937bd-0e3d-4bf7-8086-6d5b0cdb8952";
    public static final String ICONCLASS_CODE_29 = "29";
    public static final String ICONCLASS_ID_31 = "156ac40e-4407-4e49-8178-8a307ee200df";
    public static final String ICONCLASS_CODE_31 = "31";
    public static final String ICONCLASS_ID_32 = "856d961d-1f3e-4b4c-a868-af6cb80710da";
    public static final String ICONCLASS_CODE_32 = "32";
    public static final String ICONCLASS_ID_33 = "869d80d2-3e08-4cdc-9590-bd992be42e7e";
    public static final String ICONCLASS_CODE_33 = "33";
    public static final String ICONCLASS_ID_34 = "62683f61-c21b-41fe-bfa5-3d7d5a5e14c2";
    public static final String ICONCLASS_CODE_34 = "34";
    public static final String ICONCLASS_ID_35 = "b0cc538f-f856-484f-8938-d056a620b657";
    public static final String ICONCLASS_CODE_35 = "35";
    public static final String ICONCLASS_ID_36 = "ec255c37-87ef-4c24-b74f-0643f57a029f";
    public static final String ICONCLASS_CODE_36 = "36";
    public static final String ICONCLASS_ID_41 = "f9d7fd1b-c1d1-47fb-843c-8f98bcfa08ad";
    public static final String ICONCLASS_CODE_41 = "41";
    public static final String ICONCLASS_ID_42 = "031ecc38-288b-4568-9f72-4ad3306613eb";
    public static final String ICONCLASS_CODE_42 = "42";
    public static final String ICONCLASS_ID_43 = "f1aecc0c-a0bd-4e18-816b-67032f845b06";
    public static final String ICONCLASS_CODE_43 = "43";
    public static final String ICONCLASS_ID_44 = "3ecdbe3a-e2ab-4d90-a698-d504cb95a09c";
    public static final String ICONCLASS_CODE_44 = "44";
    public static final String ICONCLASS_ID_45 = "7a98c619-4265-4c30-9608-601935b41ca8";
    public static final String ICONCLASS_CODE_45 = "45";
    public static final String ICONCLASS_ID_46 = "3ec7866d-5a03-43dc-a6ee-c35cdb223b9a";
    public static final String ICONCLASS_CODE_46 = "46";
    public static final String ICONCLASS_ID_47 = "20716c2f-5e18-4019-989f-408c3b23ae33";
    public static final String ICONCLASS_CODE_47 = "47";
    public static final String ICONCLASS_ID_48 = "cca2f020-da59-404e-9033-add649002c42";
    public static final String ICONCLASS_CODE_48 = "48";
    public static final String ICONCLASS_ID_49 = "4da6e3a0-9550-4f5a-b42a-1493ef71fbb0";
    public static final String ICONCLASS_CODE_49 = "49";
    public static final String ICONCLASS_ID_51 = "94368a9a-5bef-4fc6-9dba-943812aff7fe";
    public static final String ICONCLASS_CODE_51 = "51";
    public static final String ICONCLASS_ID_52= "5e94063d-e597-4cf5-b133-880c54559231";
    public static final String ICONCLASS_CODE_52 = "52";
    public static final String ICONCLASS_ID_53 = "236b141c-b125-49f7-8c2d-46a334a3c53a";
    public static final String ICONCLASS_CODE_53 = "53";
    public static final String ICONCLASS_ID_54 = "bc2ab7ab-fd6a-4d68-af07-5f54f916a8c6";
    public static final String ICONCLASS_CODE_54 = "54";
    public static final String ICONCLASS_ID_55 = "a838b372-1b45-488d-a6d1-4b2c40ec4855";
    public static final String ICONCLASS_CODE_55 = "55";
    public static final String ICONCLASS_ID_56 = "4b5213a2-3a34-43fe-8a0e-a2282f26bfa4";
    public static final String ICONCLASS_CODE_56 = "56";
    public static final String ICONCLASS_ID_57 = "18116521-e853-4558-a93e-cd3b16f30667";
    public static final String ICONCLASS_CODE_57 = "57";
    public static final String ICONCLASS_ID_58 = "54390f1d-f59a-4f87-838f-a4402e63b5fd";
    public static final String ICONCLASS_CODE_58 = "58";
    public static final String ICONCLASS_ID_59 = "c5f7c39f-3cd4-476f-9f33-404a7b0cf554";
    public static final String ICONCLASS_CODE_59 = "59";
    public static final String ICONCLASS_ID_61 = "f4a2e897-f121-421f-b917-bc3b30e442c6";
    public static final String ICONCLASS_CODE_61 = "61";
    public static final String ICONCLASS_ID_62 = "701430fe-a40a-4b59-870e-a9f1cbf23193";
    public static final String ICONCLASS_CODE_62 = "62";
    public static final String ICONCLASS_ID_71 = "eeac4476-7f47-419e-a5bc-bffb40303c77";
    public static final String ICONCLASS_CODE_71 = "71";
    public static final String ICONCLASS_ID_72 = "a15eb639-f24e-4520-b5b6-ab94edd75b0e";
    public static final String ICONCLASS_CODE_72 = "72";
    public static final String ICONCLASS_ID_73 = "0eda23e0-8b78-47ac-88c9-b3bdc11101cc";
    public static final String ICONCLASS_CODE_73 = "73";
    public static final String ICONCLASS_ID_81 = "4308aff2-2ae0-4251-a26a-793d7d9d9b6c";
    public static final String ICONCLASS_CODE_81 = "81";
    public static final String ICONCLASS_ID_82 = "b9c55cfc-8b4b-4b7c-ad4b-4bd8cdcedeba";
    public static final String ICONCLASS_CODE_82 = "82";
    public static final String ICONCLASS_ID_83 = "0bb86dbe-f448-44f2-b2af-bd509a1adb81";
    public static final String ICONCLASS_CODE_83 = "83";
    public static final String ICONCLASS_ID_84 = "529c65eb-8bf4-4706-8a01-9d170760a016";
    public static final String ICONCLASS_CODE_84 = "84";
    public static final String ICONCLASS_ID_85 = "297c37f9-0835-43c4-a142-acb7ddc5f6be";
    public static final String ICONCLASS_CODE_85 = "85";
    public static final String ICONCLASS_ID_86 = "3d5452ea-714f-47dc-951f-fa535a3cfc10";
    public static final String ICONCLASS_CODE_86 = "86";
    public static final String ICONCLASS_ID_91 = "e78a6816-2ad6-4ca3-a17f-752e19dd5f7b";
    public static final String ICONCLASS_CODE_91 = "91";
    public static final String ICONCLASS_ID_92 = "875e366a-8691-4904-b7e5-98d33a697dbf";
    public static final String ICONCLASS_CODE_92 = "92";
    public static final String ICONCLASS_ID_93 = "3385aeb2-5846-40de-8729-afedd9f0ef4e";
    public static final String ICONCLASS_CODE_93 = "93";
    public static final String ICONCLASS_ID_94 = "2afd4ff2-e317-46c7-b805-a6cc27a2fd2d";
    public static final String ICONCLASS_CODE_94 = "94";
    public static final String ICONCLASS_ID_95 = "e4df5a6c-b4fa-43c1-82c4-8996f8d0a07f";
    public static final String ICONCLASS_CODE_95 = "95";
    public static final String ICONCLASS_ID_96 = "87420287-5ced-4e2d-b38f-15379ed71eb6";
    public static final String ICONCLASS_CODE_96 = "96";
    public static final String ICONCLASS_ID_97 = "b5c0fbcf-0d38-4d1b-9a60-d4f29d811ee0";
    public static final String ICONCLASS_CODE_97 = "97";
    public static final String ICONCLASS_ID_98 = "9bf1a8c4-98f1-4146-babf-7a98fee9406e";
    public static final String ICONCLASS_CODE_98 = "98";

    @Getter
    private IconclassRepository repository;
    private IconclassService service;
    @Override
    protected List<IconclassCategory> initializeEntities() {
        List<IconclassCategory> entries = new ArrayList<>();

        entries.add(newInstance(
                ICONCLASS_ID_1,
                ICONCLASS_CODE_1
        ));

        entries.add(newInstance(
                ICONCLASS_ID_2,
                ICONCLASS_CODE_2
        ));

        entries.add(newInstance(
                ICONCLASS_ID_3,
                ICONCLASS_CODE_3
        ));

        entries.add(newInstance(
                ICONCLASS_ID_4,
                ICONCLASS_CODE_4
        ));

        entries.add(newInstance(
                ICONCLASS_ID_5,
                ICONCLASS_CODE_5
        ));

        entries.add(newInstance(
                ICONCLASS_ID_6,
                ICONCLASS_CODE_6
        ));

        entries.add(newInstance(
                ICONCLASS_ID_7,
                ICONCLASS_CODE_7
        ));

        entries.add(newInstance(
                ICONCLASS_ID_8,
                ICONCLASS_CODE_8
        ));

        entries.add(newInstance(
                ICONCLASS_ID_9,
                ICONCLASS_CODE_9
        ));

        entries.add(newInstance(
                ICONCLASS_ID_10,
                ICONCLASS_CODE_10
        ));

        entries.add(newInstance(
                ICONCLASS_ID_11,
                ICONCLASS_CODE_11
        ));

        entries.add(newInstance(
                ICONCLASS_ID_12,
                ICONCLASS_CODE_12
        ));

        entries.add(newInstance(
                ICONCLASS_ID_13,
                ICONCLASS_CODE_13
        ));

        entries.add(newInstance(
                ICONCLASS_ID_14,
                ICONCLASS_CODE_14
        ));

        entries.add(newInstance(
                ICONCLASS_ID_20,
                ICONCLASS_CODE_20
        ));

        entries.add(newInstance(
                ICONCLASS_ID_21,
                ICONCLASS_CODE_21
        ));

        entries.add(newInstance(
                ICONCLASS_ID_22,
                ICONCLASS_CODE_22
        ));

        entries.add(newInstance(
                ICONCLASS_ID_23,
                ICONCLASS_CODE_23
        ));

        entries.add(newInstance(
                ICONCLASS_ID_24,
                ICONCLASS_CODE_24
        ));

        entries.add(newInstance(
                ICONCLASS_ID_25,
                ICONCLASS_CODE_25
        ));

        entries.add(newInstance(
                ICONCLASS_ID_26,
                ICONCLASS_CODE_26
        ));

        entries.add(newInstance(
                ICONCLASS_ID_29,
                ICONCLASS_CODE_29
        ));

        entries.add(newInstance(
                ICONCLASS_ID_31,
                ICONCLASS_CODE_31
        ));

        entries.add(newInstance(
                ICONCLASS_ID_32,
                ICONCLASS_CODE_32
        ));

        entries.add(newInstance(
                ICONCLASS_ID_33,
                ICONCLASS_CODE_33
        ));

        entries.add(newInstance(
                ICONCLASS_ID_34,
                ICONCLASS_CODE_34
        ));

        entries.add(newInstance(
                ICONCLASS_ID_35,
                ICONCLASS_CODE_35
        ));

        entries.add(newInstance(
                ICONCLASS_ID_36,
                ICONCLASS_CODE_36
        ));

        entries.add(newInstance(
                ICONCLASS_ID_41,
                ICONCLASS_CODE_41
        ));

        entries.add(newInstance(
                ICONCLASS_ID_42,
                ICONCLASS_CODE_42
        ));

        entries.add(newInstance(
                ICONCLASS_ID_43,
                ICONCLASS_CODE_43
        ));

        entries.add(newInstance(
                ICONCLASS_ID_44,
                ICONCLASS_CODE_44
        ));

        entries.add(newInstance(
                ICONCLASS_ID_45,
                ICONCLASS_CODE_45
        ));

        entries.add(newInstance(
                ICONCLASS_ID_46,
                ICONCLASS_CODE_46
        ));

        entries.add(newInstance(
                ICONCLASS_ID_47,
                ICONCLASS_CODE_47
        ));

        entries.add(newInstance(
                ICONCLASS_ID_48,
                ICONCLASS_CODE_48
        ));

        entries.add(newInstance(
                ICONCLASS_ID_49,
                ICONCLASS_CODE_49
        ));

        entries.add(newInstance(
                ICONCLASS_ID_51,
                ICONCLASS_CODE_51
        ));

        entries.add(newInstance(
                ICONCLASS_ID_52,
                ICONCLASS_CODE_52
        ));

        entries.add(newInstance(
                ICONCLASS_ID_53,
                ICONCLASS_CODE_53
        ));

        entries.add(newInstance(
                ICONCLASS_ID_54,
                ICONCLASS_CODE_54
        ));

        entries.add(newInstance(
                ICONCLASS_ID_55,
                ICONCLASS_CODE_55
        ));

        entries.add(newInstance(
                ICONCLASS_ID_56,
                ICONCLASS_CODE_56
        ));

        entries.add(newInstance(
                ICONCLASS_ID_57,
                ICONCLASS_CODE_57
        ));

        entries.add(newInstance(
                ICONCLASS_ID_58,
                ICONCLASS_CODE_58
        ));

        entries.add(newInstance(
                ICONCLASS_ID_59,
                ICONCLASS_CODE_59
        ));

        entries.add(newInstance(
                ICONCLASS_ID_61,
                ICONCLASS_CODE_61
        ));

        entries.add(newInstance(
                ICONCLASS_ID_62,
                ICONCLASS_CODE_62
        ));

        entries.add(newInstance(
                ICONCLASS_ID_71,
                ICONCLASS_CODE_71
        ));

        entries.add(newInstance(
                ICONCLASS_ID_72,
                ICONCLASS_CODE_72
        ));

        entries.add(newInstance(
                ICONCLASS_ID_73,
                ICONCLASS_CODE_73
        ));

        entries.add(newInstance(
                ICONCLASS_ID_81,
                ICONCLASS_CODE_81
        ));

        entries.add(newInstance(
                ICONCLASS_ID_82,
                ICONCLASS_CODE_82
        ));

        entries.add(newInstance(
                ICONCLASS_ID_83,
                ICONCLASS_CODE_83
        ));

        entries.add(newInstance(
                ICONCLASS_ID_84,
                ICONCLASS_CODE_84
        ));

        entries.add(newInstance(
                ICONCLASS_ID_85,
                ICONCLASS_CODE_85
        ));

        entries.add(newInstance(
                ICONCLASS_ID_86,
                ICONCLASS_CODE_86
        ));

        entries.add(newInstance(
                ICONCLASS_ID_91,
                ICONCLASS_CODE_91
        ));

        entries.add(newInstance(
                ICONCLASS_ID_92,
                ICONCLASS_CODE_92
        ));

        entries.add(newInstance(
                ICONCLASS_ID_93,
                ICONCLASS_CODE_93
        ));

        entries.add(newInstance(
                ICONCLASS_ID_94,
                ICONCLASS_CODE_94
        ));

        entries.add(newInstance(
                ICONCLASS_ID_95,
                ICONCLASS_CODE_95
        ));

        entries.add(newInstance(
                ICONCLASS_ID_96,
                ICONCLASS_CODE_96
        ));

        entries.add(newInstance(
                ICONCLASS_ID_97,
                ICONCLASS_CODE_97
        ));

        entries.add(newInstance(
                ICONCLASS_ID_98,
                ICONCLASS_CODE_98
        ));

        return entries.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private IconclassCategory newInstance(
            String id,
            String code) {
        return service.createFromInit(code, id);
    }

    @Autowired
    public void setIconclassRepository(IconclassRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setIconclassService(IconclassService service) {
        this.service = service;
    }
}
