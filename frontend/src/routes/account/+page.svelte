<svelte:options runes={true} />
<script lang="ts">
	import Input from "$components/inputs/input.svelte";
	import { session, updateProfile as storeUpdateProfile } from "$components/session/session.store";
	import { fromStore } from "svelte/store";

	let firstName = $state('');
	let lastName = $state('');
	let email = $state('');

	let retry = 0;

	let message = $state('');

	const sess = fromStore(session);

	function updateProfile(e){
		e.preventDefault();
		const obj: {firstName: string, lastName: string, email?: string} = {
			firstName,
			lastName
		};
		if (email){
			obj.email = email;
		}
		storeUpdateProfile(obj).then(session => {
			const store = fromStore(session);
			for(let key in obj){
				if (obj[key] !== store.current.userProfile[key]){
					message = "Profile update failed. Please try again. If the problem persists, please contact support.";
					return;
				}
			}
			message = "Profile updated successfully.";
		}, err => {
			message = "Profile update failed. Please try again. If the problem persists, please contact support.";
		});
	}

	function mount() {
		if (retry > 4) {
			return;
		}
		if (sess.current.userProfile === "loading"){
			retry++;
			setTimeout(mount, 500);
			return;
		}
		retry = 0;
		if (sess.current.userProfile === null) {
			return;
		}
		const user = sess.current.userProfile;
		firstName = user.firstName || '';
		lastName = user.lastName || '';
		email = user.email || '';
	}
	$effect(() => {
		mount();
	});
	
</script>
<main>
	<h1>Account Settings</h1>
	<form action="" method="post" onsubmit={updateProfile}>
		<p aria-live="assertive">{message}</p>
		<Input label="First Name" name="firstName" type="text" required={true} bind:value={firstName} />
		<Input label="Last Name" name="lastName" type="text" required={true} bind:value={lastName} />
		<button type="submit" disabled={!(firstName || lastName || email)}>Update Profile</button>
	</form>
</main>