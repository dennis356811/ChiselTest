package Pipeline
import chisel3._
import chisel3.util._

class Reg_D extends Module {
  val io = IO(new Bundle {
    val stall                   = Input(Bool())
    val jb                      = Input(Bool())
    val branch_or_not           = Input(Bool())
    val pc                      = Input(Bits(32.W))
    val inst                    = Input(Bits(32.W))

    val out_Reg_D_branch_or_not = Output(Bool())
    val out_Reg_D_pc            = Output(Bits(32.W))
    val out_Reg_D_inst          = Output(Bits(32.W))
  })
  val PC_REG                    = RegInit (0.U (32.W))
  val INST_REG                  = RegInit (0.U (32.W))
  val BRANCH_OR_NOT_REG         = RegInit (0.U (1.W))

  when(io.jb){
    PC_REG                      := 0.U(32.W)
    INST_REG                    := 0.U(32.W)
    BRANCH_OR_NOT_REG           := 0.U(1.W)
  }.elsewhen(io.stall){
    PC_REG                      := PC_REG
    INST_REG                    := INST_REG
    BRANCH_OR_NOT_REG           := BRANCH_OR_NOT_REG
  }.otherwise{
    PC_REG                      := io.pc
    INST_REG                    := io.inst
    BRANCH_OR_NOT_REG           := io.branch_or_not
  }
  io.out_Reg_D_pc               := PC_REG
  io.out_Reg_D_inst             := INST_REG
  io.out_Reg_D_branch_or_not    := BRANCH_OR_NOT_REG
}

object Reg_D extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(new Reg_D, Array("--target-dir", "generated/Reg_D"))
}
