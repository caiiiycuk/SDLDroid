#!/bin/sh
ROOT=/home/caiiiycuk/stratagus/build
ln -sf src/libsdl-1.2.so ~/android/stradroid/project/obj/local/armeabi/libSDL.so

if [ -f $ROOT/wargus ]
then
  echo "wargus binary exists, skipping build"
else
  ~/android/stradroid/project/jni/application/setEnvironment.sh sh -c "cd $ROOT && ./configure --host=arm-linux-androideabi && make clean && make -j3"
fi

cp $ROOT/wargus $ROOT/libapplication.so
cp $ROOT/wargus libapplication.so