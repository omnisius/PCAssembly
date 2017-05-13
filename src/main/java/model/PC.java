package model;

public class PC {

    private String CPU;
    private String motherboard;
    private String HDD;
    private String RAM;

    public String getCPU() {
        return CPU;
    }

    public String getMotherboard() {
        return motherboard;
    }

    public String getHDD() {
        return HDD;
    }

    public String getRAM() {
        return RAM;
    }


    private PC (PCBuilder builder) {
        this.CPU = builder.CPU;
        this.RAM = builder.RAM;
        this.HDD = builder.HDD;
        this.RAM = builder.RAM;
    }

    @Override
    public String toString() {
        return "PC{" +
                "CPU='" + CPU + '\'' +
                ", motherboard='" + motherboard + '\'' +
                ", HDD='" + HDD + '\'' +
                ", RAM='" + RAM + '\'' +
                '}';
    }

    public static class PCBuilder {
        private String CPU;
        private String motherboard;
        private String HDD;
        private String RAM;

        public PCBuilder addCPU(String cpu) {
            this.CPU = cpu;
            return this;
        }

        public PCBuilder addMotherBoard(String motherboard) {
            this.motherboard = motherboard;
            return this;
        }

        public PCBuilder addHDD(String hdd) {
            this.HDD = hdd;
            return this;
        }

        public PCBuilder addRAM(String ram) {
            this.RAM = ram;
            return this;
        }

        public PC build(){
            return new PC(this);
        }
    }
}
