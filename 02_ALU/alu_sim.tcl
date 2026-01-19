file mkdir build
cd build
exec xvlog -sv -f ../src/test/uvm/compile_list.f -L uvm -define SIM_TIMEOUT=50000 ; 

exec xelab alu_tb -relax -L uvm -s top -timescale 1ns/1ps -v 2
exec xsim top \
    -testplusarg UVM_TESTNAME=alu_test \
    -testplusarg UVM_VERBOSITY=UVM_FULL \
    -runall \
    -wdb alu_dump.wdb
