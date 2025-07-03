require 'fileutils'

def run(command)
  system(command) or abort("Failed to run: #{command}")
end

Dir.chdir("kilatec") do
  run("chmod +x Make.sh")
  run("./Make.sh -lso")
  run("ruby .scripts/make_android_compatible_so.rb")
end

# Make the script executable and run build .so

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

run("chmod +x gradlew")
run("./gradlew assembleRelease")

puts "Done."
