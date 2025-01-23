module ALU(
  input         clock,
  input         reset,
  input  [4:0]  io_opcode,
  input  [2:0]  io_func3,
  input         io_func7,
  input  [31:0] io_operand1,
  input  [31:0] io_operand2,
  output [31:0] io_alu_out
);
  wire [3:0] func7_and_func3 = {io_func7,io_func3}; // @[Cat.scala 31:58]
  wire [31:0] _io_alu_out_T = io_operand1; // @[ALU.scala 43:44]
  wire [31:0] _io_alu_out_T_1 = io_operand2; // @[ALU.scala 43:65]
  wire [31:0] _io_alu_out_T_5 = $signed(io_operand1) + $signed(io_operand2); // @[ALU.scala 43:73]
  wire [31:0] _io_alu_out_T_11 = $signed(io_operand1) - $signed(io_operand2); // @[ALU.scala 46:74]
  wire [62:0] _GEN_31 = {{31'd0}, io_operand1}; // @[ALU.scala 49:44]
  wire [62:0] _io_alu_out_T_13 = _GEN_31 << io_operand2[4:0]; // @[ALU.scala 49:44]
  wire  _io_alu_out_T_16 = $signed(io_operand1) < $signed(io_operand2); // @[ALU.scala 52:51]
  wire  _io_alu_out_T_17 = io_operand1 < io_operand2; // @[ALU.scala 55:44]
  wire [31:0] _io_alu_out_T_18 = io_operand1 ^ io_operand2; // @[ALU.scala 58:44]
  wire [31:0] _io_alu_out_T_20 = io_operand1 >> io_operand2[4:0]; // @[ALU.scala 61:44]
  wire [31:0] _io_alu_out_T_24 = $signed(io_operand1) >>> io_operand2[4:0]; // @[ALU.scala 64:73]
  wire [31:0] _io_alu_out_T_25 = io_operand1 | io_operand2; // @[ALU.scala 67:44]
  wire [31:0] _io_alu_out_T_26 = io_operand1 & io_operand2; // @[ALU.scala 70:44]
  wire [31:0] _GEN_0 = 4'h7 == func7_and_func3 ? _io_alu_out_T_26 : 32'h0; // @[ALU.scala 39:16 41:33 70:28]
  wire [31:0] _GEN_1 = 4'h6 == func7_and_func3 ? _io_alu_out_T_25 : _GEN_0; // @[ALU.scala 41:33 67:28]
  wire [31:0] _GEN_2 = 4'hd == func7_and_func3 ? _io_alu_out_T_24 : _GEN_1; // @[ALU.scala 41:33 64:28]
  wire [31:0] _GEN_3 = 4'h5 == func7_and_func3 ? _io_alu_out_T_20 : _GEN_2; // @[ALU.scala 41:33 61:28]
  wire [31:0] _GEN_4 = 4'h4 == func7_and_func3 ? _io_alu_out_T_18 : _GEN_3; // @[ALU.scala 41:33 58:28]
  wire [31:0] _GEN_5 = 4'h3 == func7_and_func3 ? {{31'd0}, io_operand1 < io_operand2} : _GEN_4; // @[ALU.scala 41:33 55:28]
  wire [31:0] _GEN_6 = 4'h2 == func7_and_func3 ? {{31'd0}, $signed(_io_alu_out_T) < $signed(_io_alu_out_T_1)} : _GEN_5; // @[ALU.scala 41:33 52:28]
  wire [62:0] _GEN_7 = 4'h1 == func7_and_func3 ? _io_alu_out_T_13 : {{31'd0}, _GEN_6}; // @[ALU.scala 41:33 49:28]
  wire [62:0] _GEN_8 = 4'h8 == func7_and_func3 ? {{31'd0}, _io_alu_out_T_11} : _GEN_7; // @[ALU.scala 41:33 46:28]
  wire [62:0] _GEN_9 = 4'h0 == func7_and_func3 ? {{31'd0}, _io_alu_out_T_5} : _GEN_8; // @[ALU.scala 41:33 43:28]
  wire  _T_12 = 3'h0 == io_func3; // @[ALU.scala 75:26]
  wire  _T_15 = 3'h4 == io_func3; // @[ALU.scala 75:26]
  wire  _T_16 = 3'h6 == io_func3; // @[ALU.scala 75:26]
  wire  _T_17 = 3'h7 == io_func3; // @[ALU.scala 75:26]
  wire  _T_18 = 3'h1 == io_func3; // @[ALU.scala 75:26]
  wire  _T_19 = 3'h5 == io_func3; // @[ALU.scala 75:26]
  wire [31:0] _GEN_10 = io_func7 ? _io_alu_out_T_24 : _io_alu_out_T_20; // @[ALU.scala 101:32 98:39 99:32]
  wire [31:0] _GEN_11 = 3'h5 == io_func3 ? _GEN_10 : 32'h0; // @[ALU.scala 39:16 75:26]
  wire [62:0] _GEN_12 = 3'h1 == io_func3 ? _io_alu_out_T_13 : {{31'd0}, _GEN_11}; // @[ALU.scala 75:26 95:28]
  wire [62:0] _GEN_13 = 3'h7 == io_func3 ? {{31'd0}, _io_alu_out_T_26} : _GEN_12; // @[ALU.scala 75:26 92:28]
  wire [62:0] _GEN_14 = 3'h6 == io_func3 ? {{31'd0}, _io_alu_out_T_25} : _GEN_13; // @[ALU.scala 75:26 89:28]
  wire [62:0] _GEN_15 = 3'h4 == io_func3 ? {{31'd0}, _io_alu_out_T_18} : _GEN_14; // @[ALU.scala 75:26 86:28]
  wire [62:0] _GEN_16 = 3'h3 == io_func3 ? {{62'd0}, _io_alu_out_T_17} : _GEN_15; // @[ALU.scala 75:26 83:28]
  wire [62:0] _GEN_17 = 3'h2 == io_func3 ? {{62'd0}, _io_alu_out_T_16} : _GEN_16; // @[ALU.scala 75:26 80:28]
  wire [62:0] _GEN_18 = 3'h0 == io_func3 ? {{31'd0}, _io_alu_out_T_5} : _GEN_17; // @[ALU.scala 75:26 77:28]
  wire [31:0] _GEN_19 = _T_17 ? {{31'd0}, io_operand1 >= io_operand2} : 32'h0; // @[ALU.scala 107:26 124:28 39:16]
  wire [31:0] _GEN_20 = _T_16 ? {{31'd0}, _io_alu_out_T_17} : _GEN_19; // @[ALU.scala 107:26 121:28]
  wire [31:0] _GEN_21 = _T_19 ? {{31'd0}, $signed(_io_alu_out_T) >= $signed(_io_alu_out_T_1)} : _GEN_20; // @[ALU.scala 107:26 118:28]
  wire [31:0] _GEN_22 = _T_15 ? {{31'd0}, _io_alu_out_T_16} : _GEN_21; // @[ALU.scala 107:26 115:28]
  wire [31:0] _GEN_23 = _T_18 ? {{31'd0}, io_operand1 != io_operand2} : _GEN_22; // @[ALU.scala 107:26 112:28]
  wire [31:0] _GEN_24 = _T_12 ? {{31'd0}, io_operand1 == io_operand2} : _GEN_23; // @[ALU.scala 107:26 109:28]
  wire [31:0] _io_alu_out_T_59 = io_operand1 + io_operand2; // @[ALU.scala 133:36]
  wire [31:0] _io_alu_out_T_61 = io_operand1 + 32'h4; // @[ALU.scala 135:36]
  wire [31:0] _GEN_25 = io_opcode == 5'h1b | io_opcode == 5'h19 ? _io_alu_out_T_61 : 32'h0; // @[ALU.scala 134:110 135:20 137:20]
  wire [31:0] _GEN_26 = io_opcode == 5'h5 | io_opcode == 5'h8 | io_opcode == 5'h0 ? _io_alu_out_T_59 : _GEN_25; // @[ALU.scala 132:155 133:20]
  wire [31:0] _GEN_27 = io_opcode == 5'hd ? io_operand2 : _GEN_26; // @[ALU.scala 130:62 131:20]
  wire [31:0] _GEN_28 = io_opcode == 5'h18 ? _GEN_24 : _GEN_27; // @[ALU.scala 106:60]
  wire [62:0] _GEN_29 = io_opcode == 5'h4 ? _GEN_18 : {{31'd0}, _GEN_28}; // @[ALU.scala 74:61]
  wire [62:0] _GEN_30 = io_opcode == 5'hc ? _GEN_9 : _GEN_29; // @[ALU.scala 40:53]
  assign io_alu_out = _GEN_30[31:0];
endmodule
