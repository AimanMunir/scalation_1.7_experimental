
#######################################################################
# check.sh - script to check generated source files:
# mem_mapped array classes   > run-main scalation.util.bld.BldMM_Array
# mem_mapped sorting classes > run-main scalation.util.bld.BldMM_Sorting

#######################################################################
# check memory mapped array classes

echo diff MM_ArrayI.scalaa ../MM_ArrayI.scala
diff MM_ArrayI.scalaa ../MM_ArrayI.scala

echo diff MM_ArrayL.scalaa ../MM_ArrayL.scala
diff MM_ArrayL.scalaa ../MM_ArrayL.scala

echo diff MM_ArrayD.scalaa ../MM_ArrayD.scala
diff MM_ArrayD.scalaa ../MM_ArrayD.scala

echo diff MM_ArrayC.scalaa ../MM_ArrayC.scala
diff MM_ArrayC.scalaa ../MM_ArrayC.scala

echo diff MM_ArrayR.scalaa ../MM_ArrayR.scala
diff MM_ArrayR.scalaa ../MM_ArrayR.scala

echo diff MM_ArrayQ.scalaa ../MM_ArrayQ.scala
diff MM_ArrayQ.scalaa ../MM_ArrayQ.scala

## Note, MM_Array for StrNum is dissimilar from the others, so it requires hand coding

# echo diff MM_ArrayS.scalaa ../MM_ArrayS.scala
# diff MM_ArrayS.scalaa ../MM_ArrayS.scala

## Note, MM_Array for TimeNum is dissimilar from the others, so it requires hand coding

# echo diff MM_ArrayT.scalaa ../MM_ArrayT.scala
# diff MM_ArrayT.scalaa ../MM_ArrayT.scala


#######################################################################
# check memory mapped sorting classes

echo diff MM_SortingI.scalaa ../MM_SortingI.scala
diff MM_SortingI.scalaa ../MM_SortingI.scala

echo diff MM_SortingL.scalaa ../MM_SortingL.scala
diff MM_SortingL.scalaa ../MM_SortingL.scala

echo diff MM_SortingD.scalaa ../MM_SortingD.scala
diff MM_SortingD.scalaa ../MM_SortingD.scala

echo diff MM_SortingC.scalaa ../MM_SortingC.scala
diff MM_SortingC.scalaa ../MM_SortingC.scala

echo diff MM_SortingR.scalaa ../MM_SortingR.scala
diff MM_SortingR.scalaa ../MM_SortingR.scala

echo diff MM_SortingQ.scalaa ../MM_SortingQ.scala
diff MM_SortingQ.scalaa ../MM_SortingQ.scala

echo diff MM_SortingS.scalaa ../MM_SortingS.scala
diff MM_SortingS.scalaa ../MM_SortingS.scala

echo diff MM_SortingT.scalaa ../MM_SortingT.scala
diff MM_SortingT.scalaa ../MM_SortingT.scala


#######################################################################
# check sorting classes

echo diff SortingI.scalaa ../SortingI.scala
diff SortingI.scalaa ../SortingI.scala

echo diff SortingL.scalaa ../SortingL.scala
diff SortingL.scalaa ../SortingL.scala

echo diff SortingD.scalaa ../SortingD.scala
diff SortingD.scalaa ../SortingD.scala

echo diff SortingC.scalaa ../SortingC.scala
diff SortingC.scalaa ../SortingC.scala

echo diff SortingR.scalaa ../SortingR.scala
diff SortingR.scalaa ../SortingR.scala

echo diff SortingQ.scalaa ../SortingQ.scala
diff SortingQ.scalaa ../SortingQ.scala

echo diff SortingS.scalaa ../SortingS.scala
diff SortingS.scalaa ../SortingS.scala

echo diff SortingT.scalaa ../SortingT.scala
diff SortingT.scalaa ../SortingT.scala

