require 'fileutils'

def run(command)
  system(command) or abort("Failed to run: #{command}")
end

gradle = false
termux = false

ARGV.each do |arg|
  case arg
    when "--gradle", "-g"
      gradle = true
    when "--termux", "-t"
      termux = true
    else
      puts "Unknown arg: #{arg}"
  end
end

Dir.chdir("kilatec") do
  run("chmod +x Make.sh")
  if termux
    run("bash Make.sh -lso")
  else
    run("./Make.sh -lso")
  end
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

if gradle
  run("chmod +x gradlew")
  run("./gradlew assembleRelease")
end

puts "Done."