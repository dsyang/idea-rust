//static U : *mut u32 = 0 as u32;
static mut DIRECTIVES: *const Vec<Option<u32>> =
   0 as *const Vec<Option<u32>>;

fn main() {
   let i: u32 = 1;
   // explicit cast
   let p_imm: *const u32 = &i as *const u32;
   let mut m: u32 = 2;
   // implicit coercion
   let p_mut: *mut u32 = &mut m;

   unsafe {
       let ref_imm: &u32 = &*p_imm;
       let ref_mut: &mut u32 = &mut *p_mut;

       println!("{}, {}", ref_imm, ref_mut);
   }

}