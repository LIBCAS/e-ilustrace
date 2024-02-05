package cz.inqool.eas.common.module;

public class Modules {
    public static ModuleDefinition EAS = new ModuleDefinition("8f845454-89bf-43a4-8eb2-464ee2283354", "Jádro EAS");

    public static ModuleDefinition AUDIT_LOG = new ModuleDefinition("b115c0f1-3dbd-4d7f-80aa-60888b87002b", "Evidence logů");
    public static ModuleDefinition REPORTING = new ModuleDefinition("14401ed3-0ebc-408c-814f-b131f4bd7810", "Modul reportingu");
    public static ModuleDefinition EXPORT = new ModuleDefinition("d88e269b-effe-43ac-931f-859090d873dd", "Evidence šablon exportů");
    public static ModuleDefinition SCHEDULING = new ModuleDefinition("de3b7085-5558-4473-a9b4-6f1c9a52062d", "Evidence pravidelně spouštěných úloh");

    public static ModuleDefinition ACTIONS = new ModuleDefinition("425e7b7d-3506-429a-a206-69076f5f568b", "Evidence skriptů");
    public static ModuleDefinition HISTORY = new ModuleDefinition("9a66085b-cc4c-4046-b6ea-cdf80c9d3d5e", "Modul historie objektů");
    public static ModuleDefinition SEQUENCES = new ModuleDefinition("6e94bc9d-0c37-4669-85cc-ad1aa3267139", "Evidence časových řad");
    public static ModuleDefinition CERTIFICATES = new ModuleDefinition("b45b37e6-2be6-42a6-8f95-0c1cd3ea7b59", "Evidence certifikátů");

    public static ModuleDefinition SIGNING = new ModuleDefinition("8c17aafa-4509-4067-bdd7-d49cc578fefa", "Modul podepisování");
    public static ModuleDefinition ESS = new ModuleDefinition("b46712ec-e1b9-4283-9cb1-cb9f7e65f6ce", "Modul integrace spisové služby");
    public static ModuleDefinition ISZR = new ModuleDefinition("01e17741-4fd6-46d8-af60-9f7453ac6287", "Modul integrace ISZR");
}
