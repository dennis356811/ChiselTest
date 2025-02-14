/*import chisel3._
import chisel3.util._
object ControllerOpcodeDefition{
    val I1      = "b00000".U(5.W)
    val I2      = "b00100".U(5.W)
    val R       = "b01100".U(5.W)
    val Jalr    = "b11001".U(5.W)
    val B       = "b11000".U(5.W)
    val S       = "b01000".U(5.W)
    val lui     = "b01101".U(5.W)
    val auipc   = "b00101".U(5.W)
    val Jal     = "b11011".U(5.W)
}

class Controller extends Module {
    val io = IO(new Bundle {
        val op              = Input(Bits(5.W))
        val f3              = Input(Bits(3.W))
        val rd              = Input(Bits(5.W))
        val rs1             = Input(Bits(5.W))
        val rs2             = Input(Bits(5.W))
        val f7              = Input(Bool())
        val alu_out         = Input(Bool())

        //stall & jb
        val stall           = Output(Bool())
        val next_pc_sel     = Output(Bool())

        //IF/ID
        val F_im_w_en       = Output(Bits(4.W))
        val D_rs1_data_sel  = Output(Bool())
        val D_rs2_data_sel  = Output(Bool())

        //EX
        val E_rs1_data_sel  = Output(Bits(2.W))
        val E_rs2_data_sel  = Output(Bits(2.W))
        val E_jb_op1_sel    = Output(Bool())
        val E_alu_op1_sel   = Output(Bool())
        val E_alu_op2_sel   = Output(Bool())
        val E_op            = Output(Bits(5.W))
        val E_f3            = Output(Bits(3.W))
        val E_f7            = Output(Bool())

        //MEM
        val M_dm_w_en       = Output(Bits(4.W))

        //WB
        val W_wb_en         = Output(Bool())
        val W_rd_index      = Output(Bits(5.W))
        val W_f3            = Output(Bits(3.W))
        val W_wb_data_sel   = Output(Bool())
    })
    val IN_E_OP_REG         = RegInit(0.U(5.W))
    val IN_E_F3_REG         = RegInit(0.U(3.W))
    val IN_E_RD_REG         = RegInit(0.U(5.W))
    val IN_E_RS1_REG        = RegInit(0.U(5.W))
    val IN_E_RS2_REG        = RegInit(0.U(5.W))
    val IN_E_F7_REG         = RegInit(0.U(1.W))

    val IN_M_OP_REG         = RegInit(0.U(5.W))
    val IN_M_F3_REG         = RegInit(0.U(3.W))
    val IN_M_RD_REG         = RegInit(0.U(5.W))

    val IN_W_OP_REG         = RegInit(0.U(5.W))
    val IN_W_F3_REG         = RegInit(0.U(3.W))
    val IN_W_RD_REG         = RegInit(0.U(5.W))

    when(io.stall || io.next_pc_sel){
        // refeash E stage
        IN_E_OP_REG     := ControllerOpcodeDefition.R
        IN_E_F3_REG     := 0.U(3.W)
        IN_E_RD_REG     := 0.U(5.W)
        IN_E_RS1_REG    := 0.U(5.W)
        IN_E_RS2_REG    := 0.U(5.W)
        IN_E_F7_REG     := 0.U(1.W)

        // refeash M stage
        IN_M_OP_REG     := IN_E_OP_REG
        IN_M_F3_REG     := IN_E_F3_REG
        IN_M_RD_REG     := IN_E_RD_REG

        // refeash W stage
        IN_W_OP_REG     := IN_M_OP_REG
        IN_W_F3_REG     := IN_M_F3_REG
        IN_W_RD_REG     := IN_M_RD_REG
    }.otherwise{
        // refeash E stage
        IN_E_OP_REG     := io.op
        IN_E_F3_REG     := io.f3
        IN_E_RD_REG     := io.rd
        IN_E_RS1_REG    := io.rs1
        IN_E_RS2_REG    := io.rs2
        IN_E_F7_REG     := io.f7

        // refeash M stage
        IN_M_OP_REG     := IN_E_OP_REG
        IN_M_F3_REG     := IN_E_F3_REG
        IN_M_RD_REG     := IN_E_RD_REG

        // refeash W stage
        IN_W_OP_REG     := IN_M_OP_REG
        IN_W_F3_REG     := IN_M_F3_REG
        IN_W_RD_REG     := IN_M_RD_REG
    }


    //decide D_rs1_data_sel, D_rs2_data_sel
    val is_D_use_rs1 = Wire(Bool())
    val is_D_use_rs2 = Wire(Bool())
    val is_W_use_rd = Wire(Bool())
    val is_D_rs1_W_rd_overlap = Wire(Bool())
    val is_D_rs2_W_rd_overlap = Wire(Bool())

    val is_E_use_rs1 = Wire(Bool())
    val is_E_use_rs2 = Wire(Bool())
    val is_M_use_rd = Wire(Bool())
    val is_E_rs1_W_rd_overlap = Wire(Bool())
    val is_E_rs1_M_rd_overlap = Wire(Bool())
    val is_E_rs2_W_rd_overlap = Wire(Bool())
    val is_E_rs2_M_rd_overlap = Wire(Bool())

    val is_DE_overlap = Wire(Bool())
    val is_D_rs1_E_rd_overlap = Wire(Bool())
    val is_D_rs2_E_rd_overlap = Wire(Bool())

    // rs1 used
    when(
        io.op === ControllerOpcodeDefition.lui || 
        io.op === ControllerOpcodeDefition.auipc || 
        io.op === ControllerOpcodeDefition.Jal
    ){
        is_D_use_rs1 := false.B
    }.otherwise{
        is_D_use_rs1 := true.B
    }

    // rs2 used
    when(
        io.op === ControllerOpcodeDefition.R || 
        io.op === ControllerOpcodeDefition.S || 
        io.op === ControllerOpcodeDefition.B
    ){
        is_D_use_rs2 := true.B
    }.otherwise{
        is_D_use_rs2 := false.B
    }

    //decide W_rd
    when (
        IN_W_OP_REG === ControllerOpcodeDefition.B || 
        IN_W_OP_REG === ControllerOpcodeDefition.S
    ) {
        is_W_use_rd := false.B;
    }.otherwise{
        is_W_use_rd := true.B;
    }
    is_D_rs1_W_rd_overlap := is_D_use_rs1 && is_W_use_rd && (io.rs1 === IN_W_RD_REG) && (IN_W_RD_REG =/= false.B);
    is_D_rs2_W_rd_overlap := is_D_use_rs2 && is_W_use_rd && (io.rs2 === IN_W_RD_REG) && (IN_W_RD_REG =/= false.B);
    io.D_rs1_data_sel := Mux(is_D_rs1_W_rd_overlap, true.B, false.B);
    io.D_rs2_data_sel := Mux(is_D_rs2_W_rd_overlap, true.B, false.B);


    //decide E_rs1_data_sel, E_rs2_data_sel
    //rs1 used
    when(
        IN_E_OP_REG === ControllerOpcodeDefition.lui || 
        IN_E_OP_REG === ControllerOpcodeDefition.auipc || 
        IN_E_OP_REG === ControllerOpcodeDefition.Jal
    ){
        is_E_use_rs1 := false.B
    }.otherwise{
        is_E_use_rs1 := true.B
    }

    //rs2 used
    when(
        IN_E_OP_REG === ControllerOpcodeDefition.R || 
        IN_E_OP_REG === ControllerOpcodeDefition.S || 
        IN_E_OP_REG === ControllerOpcodeDefition.B
    ){
        is_E_use_rs2 := true.B
    }.otherwise{
        is_E_use_rs2 := false.B
    }

    //decide W_rd
    when (
        IN_W_OP_REG === ControllerOpcodeDefition.B || 
        IN_W_OP_REG === ControllerOpcodeDefition.S
    ) {
        is_W_use_rd := false.B;
    }.otherwise{
        is_W_use_rd := true.B;
    }

    //decide M_use_rd
    when (
        IN_M_OP_REG === ControllerOpcodeDefition.B || 
        IN_M_OP_REG === ControllerOpcodeDefition.S
    ) {
        is_M_use_rd := false.B;
    }.otherwise{
        is_M_use_rd := true.B;
    }
    is_E_rs1_W_rd_overlap := is_E_use_rs1 && is_W_use_rd && (IN_E_RS1_REG === IN_W_RD_REG) && (IN_W_RD_REG =/= false.B);
    is_E_rs1_M_rd_overlap := is_E_use_rs1 && is_M_use_rd && (IN_E_RS1_REG === IN_M_RD_REG) && (IN_M_RD_REG =/= false.B);
    io.E_rs1_data_sel  := Mux(is_E_rs1_M_rd_overlap, 1.U(2.W), Mux(is_E_rs1_W_rd_overlap, 0.U(2.W), 2.U(2.W)));
    is_E_rs2_W_rd_overlap := is_E_use_rs2 && is_W_use_rd && (IN_E_RS2_REG === IN_W_RD_REG) && (IN_W_RD_REG =/= false.B);
    is_E_rs2_M_rd_overlap := is_E_use_rs2 && is_M_use_rd && (IN_E_RS2_REG === IN_M_RD_REG) && (IN_M_RD_REG =/= false.B);
    io.E_rs2_data_sel  := Mux(is_E_rs2_M_rd_overlap, 1.U(2.W), Mux(is_E_rs2_W_rd_overlap, 0.U(2.W), 2.U(2.W)));

    // decide stall
    // rs1 used
    when(
        io.op === ControllerOpcodeDefition.lui || 
        io.op === ControllerOpcodeDefition.auipc || 
        io.op === ControllerOpcodeDefition.Jal
    ){
        is_D_use_rs1 := false.B
    }.otherwise{
        is_D_use_rs1 := true.B
    }
    //rs2 used
    when(
        io.op === ControllerOpcodeDefition.R || 
        io.op === ControllerOpcodeDefition.S || 
        io.op === ControllerOpcodeDefition.B
    ){
        is_D_use_rs2 := true.B
    }.otherwise{
        is_D_use_rs2 := false.B
    }
    is_D_rs1_E_rd_overlap := is_D_use_rs1 && (io.rs1 === IN_E_RD_REG) && (IN_E_RD_REG =/= false.B);
    is_D_rs2_E_rd_overlap := is_D_use_rs2 && (io.rs2 === IN_E_RD_REG) && (IN_E_RD_REG =/= false.B);
    is_DE_overlap := (is_D_rs1_E_rd_overlap || is_D_rs2_E_rd_overlap);
    io.stall := (IN_E_OP_REG === ControllerOpcodeDefition.I1) && is_DE_overlap;
    

    //E control signal
    io.E_op := IN_E_OP_REG;
    io.E_f3 := IN_E_F3_REG;
    io.E_f7 := IN_E_F7_REG;

    //default value
    io.next_pc_sel      := false.B;
    io.E_jb_op1_sel     := false.B;
    io.E_alu_op1_sel    := false.B;
    io.E_alu_op2_sel    := false.B;
    //switch with in_E_op_reg
    switch(IN_E_OP_REG){
        is(ControllerOpcodeDefition.Jal){
            io.next_pc_sel      := true.B;
            io.E_jb_op1_sel     := false.B;
            io.E_alu_op1_sel    := false.B;
            io.E_alu_op2_sel    := false.B;
        }
        is(ControllerOpcodeDefition.Jalr){
            io.next_pc_sel      := true.B;
            io.E_jb_op1_sel     := true.B;
            io.E_alu_op1_sel    := false.B;
            io.E_alu_op2_sel    := false.B;
        }
        is(ControllerOpcodeDefition.B){
            io.next_pc_sel      := io.alu_out;
            io.E_jb_op1_sel     := false.B;
            io.E_alu_op1_sel    := true.B;
            io.E_alu_op2_sel    := true.B;
        }
        is(ControllerOpcodeDefition.R){
            io.next_pc_sel      := false.B;
            io.E_jb_op1_sel     := false.B;
            io.E_alu_op1_sel    := true.B;
            io.E_alu_op2_sel    := true.B;
        }
        is(ControllerOpcodeDefition.I2){
            io.next_pc_sel      := false.B;
            io.E_jb_op1_sel     := false.B;
            io.E_alu_op1_sel    := true.B;
            io.E_alu_op2_sel    := false.B;
        }
        is(ControllerOpcodeDefition.I1){
            io.next_pc_sel      := false.B;
            io.E_jb_op1_sel     := false.B;
            io.E_alu_op1_sel    := true.B;
            io.E_alu_op2_sel    := false.B;
        }
        is(ControllerOpcodeDefition.S){
            io.next_pc_sel      := false.B;
            io.E_jb_op1_sel     := false.B;
            io.E_alu_op1_sel    := true.B;
            io.E_alu_op2_sel    := false.B;
        }
        is(ControllerOpcodeDefition.auipc){
            io.next_pc_sel      := false.B;
            io.E_jb_op1_sel     := false.B;
            io.E_alu_op1_sel    := false.B;
            io.E_alu_op2_sel    := false.B;
        }
        is(ControllerOpcodeDefition.lui){
            io.next_pc_sel      := false.B;
            io.E_jb_op1_sel     := false.B;
            io.E_alu_op1_sel    := false.B;
            io.E_alu_op2_sel    := false.B;
        }
    }

    //M control signal
    when(IN_M_OP_REG === ControllerOpcodeDefition.S){
        io.M_dm_w_en := 0.U(4.W)
        switch(IN_M_F3_REG){
            is("b000".U){
                io.M_dm_w_en := "b0001".U(4.W)
            }
            is("b001".U){
                io.M_dm_w_en := "b0011".U(4.W)
            }
            is("b010".U){
                io.M_dm_w_en := "b1111".U(4.W)
            }
        }
    }.otherwise{
        io.M_dm_w_en := 0.U(4.W)
    }

    // W control signal
    io.W_rd_index   := IN_W_RD_REG
    io.W_f3         := IN_W_F3_REG

    when(
        IN_W_OP_REG === ControllerOpcodeDefition.B ||
        IN_W_OP_REG === ControllerOpcodeDefition.S
    ){
        io.W_wb_en := false.B
        io.W_wb_data_sel := false.B
    }.otherwise{
        io.W_wb_en := true.B
        when(IN_W_OP_REG === ControllerOpcodeDefition.I1){
            io.W_wb_data_sel := false.B
        }.otherwise{
            io.W_wb_data_sel := true.B
        }
    }
    io.F_im_w_en := 0.U(4.W)
}   
*/
//object Controller extends App {
//    (new chisel3.stage.ChiselStage).emitVerilog(new Controller, Array("--target-dir", "generated/Controller"))
//}
