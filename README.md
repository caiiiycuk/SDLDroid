Android build
=============

Directory layout:
```sh
./android-ndk-r8e - NDK
./dune2           - OpenDUNE 
./SDLDroid        - This repository
```

Making symlinks:
```sh
ln -s dune2 SDLDroid/src
```

Building:
```sh
cd SDLDroid
./build.sh
```
