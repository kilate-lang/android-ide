require 'fileutils'

def run(command)
  system(command) or abort("Failed to run: #{command}")
end

def help()
  puts "Kilate-IDE Buildscript"
  puts "  --gradle or -g               | Builds the APK with gradle."
  puts "  --termux or -t               | Will fix the termux permissions(bash instead sh)."
  puts "  --build-libkilate-so or -bls | Builds the NECESSARY libs for using kilatec in the IDE."
  puts "  --help or -h                 | Shows helps."
  exit 1
end

gradle_op = false
termux_op = false
build_libkilateso_op = false

ARGV.each do |arg|
  case arg
    when "--gradle", "-g"
      gradle_op = true
    when "--termux", "-t"
      termux_op = true
    when "--build-libkilate-so", "-bls"
      build_libkilateso_op = true
    when "--help", "-h"
      help()
    else
      puts "Unknown arg: #{arg}"
  end
end

if !gradle_op and !termux_op and !build_libkilateso_op
  help()
end

if build_libkilateso_op
  Dir.chdir("kilatec") do
    run("chmod +x Make.sh")
    if termux_op
      run("bash Make.sh -lso")
    else
      run("./Make.sh -lso")
    end
    run("ruby .scripts/make_android_compatible_so.rb")
  end
 
  # Target directory
  jni_libs_dir = "app/src/main/jniLibs"
  # FileUtils.mkdir_p(jni_libs_dir)

  # Copy compatible Android libraries
  source_dir = "kilatec/build/android/lib"
  puts "Copying .so libs from #{source_dir} to #{jni_libs_dir}"

  FileUtils.rm_rf(jni_libs_dir)
  FileUtils.mkdir_p(jni_libs_dir)

  # Make the copy
  FileUtils.cp_r(Dir["#{source_dir}/*"], jni_libs_dir)
end

if gradle_op
  run("chmod +x gradlew")
  if termux_op
    run("bash gradlew assembleRelease")
  else
    run("./gradlew assembleRelease")
  end
end

puts "Done."